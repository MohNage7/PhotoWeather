package com.mohnage7.weather.features.camera.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.mohnage7.weather.OnFragmentInteractionListener;
import com.mohnage7.weather.R;
import com.mohnage7.weather.databinding.FragmentCameraBinding;
import com.mohnage7.weather.features.camera.CustomCameraManager;
import com.mohnage7.weather.features.camera.callback.CameraHandler;
import com.mohnage7.weather.features.camera.callback.CameraInteractionListener;
import com.mohnage7.weather.features.camera.callback.UiProvider;

import java.io.File;

import static com.mohnage7.weather.features.weatherphoto.view.ui.WeatherPhotoFragment.PHOTO_EXTRA;


public class CameraFragment extends Fragment implements CameraHandler, UiProvider, CameraInteractionListener {


    private OnFragmentInteractionListener mListener;

    private FragmentCameraBinding binding;
    private CustomCameraManager customCameraManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);
        binding.setCameraHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCamera();
    }

    private void initCamera() {
        customCameraManager = new CustomCameraManager(getContext(), getLifecycle(), this, this);
        // enable @CustomCameraManager to observe life cycle changes of @CameraFragment
        getLifecycle().addObserver(customCameraManager);
    }


    @Override
    public void onPhotoCaptureClicked() {
        customCameraManager.onPhotoCaptureClicked();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setToolbarTitle(getString(R.string.capture_photo));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public TextureView getTextureView() {
        return binding.textureView;
    }

    @Override
    public View getShutterView() {
        return binding.mShutter;
    }

    @Override
    public void onPhotoCaptureSuccess(File imageFile) {
        if (imageFile != null && imageFile.getPath() != null) {
            Bundle bundle = new Bundle();
            bundle.putString(PHOTO_EXTRA, imageFile.getPath());
            mListener.navigate(R.id.action_cameraFragment_to_weatherFragment, bundle);
        } else {
            onPhotoCaptureFailure(null);
        }
    }


    @Override
    public void onPhotoCaptureFailure(@Nullable String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.capture_failure), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
