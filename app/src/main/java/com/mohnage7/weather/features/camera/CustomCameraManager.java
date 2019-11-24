package com.mohnage7.weather.features.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.mohnage7.weather.R;
import com.mohnage7.weather.features.camera.callback.CameraInteractionListener;
import com.mohnage7.weather.features.camera.callback.UiProvider;
import com.mohnage7.weather.features.camera.view.CameraFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class CustomCameraManager implements LifecycleObserver {

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final String IMAGE_FORMAT = ".jpg";
    private static final String TAG = CameraFragment.class.getSimpleName();
    
    private CameraManager cameraManager;
    private int cameraFacing;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Size previewSize;
    private String mCameraId;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraDevice.StateCallback stateCallback;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;
    private CameraCaptureSession mCameraCaptureSession;
    private UiProvider uiProvider;
    private Context context;
    private CameraInteractionListener cameraInteractionListener;
    private File imageFile;
    private Lifecycle lifecycle;


    public CustomCameraManager(Context context,Lifecycle lifecycle ,UiProvider uiProvider,CameraInteractionListener cameraInteractionListener) {
        this.uiProvider = uiProvider;
        this.context = context;
        this.cameraInteractionListener =cameraInteractionListener;
        this.lifecycle = lifecycle;
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        setTextureListener();
        setCameraStateCallback();
    }

    private void setCameraStateCallback() {
        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice cameraDevice) {
                mCameraDevice = cameraDevice;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                cameraDevice.close();
                mCameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                mCameraDevice = null;
            }
        };
    }

    private void setTextureListener() {
        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {}
        };
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
//            if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                cameraManager.openCamera(mCameraId, stateCallback, backgroundHandler);
       //     }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }


    private File createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File galleryFolder = new File(storageDirectory, context.getResources().getString(R.string.app_name));
        if (!galleryFolder.exists()) {
            boolean wasCreated = galleryFolder.mkdirs();
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory");
            }
        }
        return galleryFolder;
    }

    private File createImageFile(File galleryFolder) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "image_" + timeStamp + "_";
        return File.createTempFile(imageFileName, IMAGE_FORMAT, galleryFolder);
    }

    private void createPreviewSession() {
        try {
            SurfaceTexture surfaceTexture = uiProvider.getTextureView().getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            if (mCameraDevice == null) {
                                return;
                            }
                            try {
                                captureRequest = captureRequestBuilder.build();
                                mCameraCaptureSession = cameraCaptureSession;
                                mCameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (streamConfigurationMap != null) {
                        previewSize = chooseOptimalSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class));
                        mCameraId = cameraId;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private Size chooseOptimalSize(Size[] outputSizes) {
        double preferredRatio = Resources.getSystem().getDisplayMetrics().heightPixels / Resources.getSystem().getDisplayMetrics().widthPixels;
        Size currentOptimalSize = outputSizes[0];
        double currentOptimalRatio = currentOptimalSize.getWidth() / (double) currentOptimalSize.getHeight();
        for (Size currentSize : outputSizes) {
            double currentRatio = currentSize.getWidth() / (double) currentSize.getHeight();
            if (Math.abs(preferredRatio - currentRatio) <
                    Math.abs(preferredRatio - currentOptimalRatio)) {
                currentOptimalSize = currentSize;
                currentOptimalRatio = currentRatio;
            }
        }
        return currentOptimalSize;
    }

    public void onPhotoCaptureClicked() {
        FileOutputStream outputPhoto = null;
        animateShutter();
        try {
            imageFile =createImageFile(createImageGallery());
            outputPhoto = new FileOutputStream(imageFile);
            uiProvider.getTextureView().getBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, outputPhoto);
        } catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
        } finally {
            try {
                if (outputPhoto != null) {
                    outputPhoto.close();
                }
            } catch (IOException e) {
                Log.e(TAG,e.getLocalizedMessage());
            }
            if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)){
                cameraInteractionListener.onPhotoCaptureSuccess(imageFile);
            }
        }
    }
    private void animateShutter() {
        uiProvider.getShutterView().setVisibility(View.VISIBLE);
        uiProvider.getShutterView().setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(uiProvider.getShutterView(), "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(uiProvider.getShutterView(), "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                uiProvider.getShutterView().setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }


    private void closeCamera() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        openBackgroundThread();
        if (uiProvider.getTextureView().isAvailable()) {
            setUpCamera();
            openCamera();
        } else {
            uiProvider.getTextureView().setSurfaceTextureListener(surfaceTextureListener);
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        closeCamera();
        closeBackgroundThread();
    }
}
