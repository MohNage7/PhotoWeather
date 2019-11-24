package com.mohnage7.weather.features.weatherphoto.view.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.mohnage7.weather.OnFragmentInteractionListener;
import com.mohnage7.weather.R;
import com.mohnage7.weather.databinding.FragmentWeatherPhotoBinding;
import com.mohnage7.weather.features.weatherphoto.view.callback.WeatherPhotoHandler;
import com.mohnage7.weather.features.weatherphoto.viewmodel.WeatherPhotoViewModel;
import com.mohnage7.weather.model.WeatherModel;
import com.mohnage7.weather.utils.PermissionManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.mohnage7.weather.features.share.ShareFragment.WEATHER_PHOTO_EXTRA;
import static com.mohnage7.weather.features.weatherphoto.view.ui.LocationManager.REQUEST_CHECK_SETTINGS;
import static com.mohnage7.weather.utils.PermissionManager.LOCATION_PERMISSION_REQUEST_CODE;


public class WeatherPhotoFragment extends Fragment implements WeatherPhotoHandler, LocationManagerInteraction {

    public static final String PHOTO_EXTRA = "photo";
    private static final String TAG = WeatherPhotoFragment.class.getSimpleName();
    private String photoPath;
    private FragmentWeatherPhotoBinding binding;
    private LocationManager locationManager;
    private Location currentLocation;
    private WeatherPhotoViewModel weatherPhotoViewModel;
    private Activity activity;
    private OnFragmentInteractionListener mListener;
    private FileObserver observer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(PHOTO_EXTRA)) {
            photoPath = bundle.getString(PHOTO_EXTRA);
        }
        activity = getActivity();
        listenToWeatherDataChanges();
        initLocationManager();
    }

    private void initLocationManager() {
        if (isLocationPermissionGranted()) {
            locationManager = new LocationManager(getActivity(), this);
        } else {
            checkForLocationPermission();
        }
    }

    private void listenToWeatherDataChanges() {
        weatherPhotoViewModel = ViewModelProviders.of(this).get(WeatherPhotoViewModel.class);
        // listen to weather data changes
        weatherPhotoViewModel.getWeatherData().observe(this, dataWrapper -> {
            switch (dataWrapper.status) {
                case LOADING:
                    showLoading();
                    break;
                case ERROR:
                    hideLoading();
                    showErrorMessage(dataWrapper.message);
                    break;
                case SUCCESS:
                    handleWeatherData(dataWrapper.data);
                    break;
                default:
                    break;
            }
        });
    }

    private void handleWeatherData(WeatherModel weatherModel) {
        if (weatherModel != null) {
            Bitmap bitmap = generateWeatherDataOverTheImage(weatherModel);
            // observe @photoPath file changes
            observeFileChanges(photoPath);
            // replace photo on disk.
            replaceOriginalBitmapWithGeneratedBitmap(bitmap);
            // insert into db.
            weatherPhotoViewModel.saveWeatherPhoto(photoPath);
        }
    }

    /**
     * Set up a file observer to watch this directory on disk
     * this observer will be triggered multiple times with @{@link FileObserver.CREATE}}
     * and @{@link FileObserver.MODIFY}, we are interested only
     * in @{@link FileObserver.CLOSE_WRITE}} which indicates that our file has been created and we can use it
     *
     * Navigate to share fragment @{@link FileObserver.CLOSE_WRITE}}event is triggered
     */
    private void observeFileChanges(String photoPath) {
        if (observer == null) {
            Log.d(TAG, "FileObserver Created");
            observer = new FileObserver(photoPath) {
                @Override
                public void onEvent(int event, String file) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        hideLoading();
                        if (event == CLOSE_WRITE) {
                            navigateToShareFragment(photoPath);
                        }
                    });
                }
            };
        }
        observer.startWatching(); //START OBSERVING
    }


    private void navigateToShareFragment(String photoPath) {
        Bundle bundle = new Bundle();
        bundle.putString(WEATHER_PHOTO_EXTRA, photoPath);
        mListener.navigate(R.id.action_weatherFragment_to_shareFragment, bundle);
    }

    /**
     * this method sets all required views over the image and then convert the ViewGroup to bitmap.
     *
     * @param weatherModel data that will be displayed in the views
     * @return generated bitmap that holds Weather Photo with Weather Data overlays it.
     */
    private Bitmap generateWeatherDataOverTheImage(WeatherModel weatherModel) {
        setViews(weatherModel);
        return convertViewToBitmap(binding.weatherPhotoLayout);
    }

    private void setViews(WeatherModel weatherModel) {
        binding.locationNameTv.setText(String.format("%s, %s", weatherModel.getName(), weatherModel.getCountryName()));
        binding.tempStatusTv.setText(weatherModel.getTempStatus());
        binding.tempTv.setText(String.format("%s°", String.valueOf(weatherModel.getTemp())));
        binding.minMaxTv.setText(String.format("%s ° / %s °", weatherModel.getMaxTemp(), weatherModel.getMinTemp()));
        Picasso.get().load(weatherModel.getTempIconURL()).into(binding.temperatureStatusIv);
        setOverlayVisible(true);
    }

    private Bitmap convertViewToBitmap(ConstraintLayout v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    private void replaceOriginalBitmapWithGeneratedBitmap(Bitmap capturedImageBitmap) {
        Bitmap newBitmap = capturedImageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        File newModifiedCapturedImage = new File(photoPath);
        if (!newModifiedCapturedImage.exists()) {
            try {
                boolean wasCreated = newModifiedCapturedImage.createNewFile();
                if (!wasCreated) {
                    Log.e(TAG, "Failed to create directory");
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        } else {
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(newModifiedCapturedImage);
                newBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.getFD().sync();
                        outputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }

            }
        }

    }

    private void showErrorMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather_photo, container, false);
        binding.setHandler(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (photoPath != null && !photoPath.isEmpty()) {
            Picasso.get().load(new File(photoPath)).into(binding.weatherPhotoIv);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setToolbarTitle(getString(R.string.generate_photo));
        setOverlayVisible(currentLocation != null);
    }

    private void checkForLocationPermission() {
        PermissionManager.checkForPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, PermissionManager.LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean isLocationPermissionGranted() {
        return PermissionManager.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.stopLocationUpdates();
        }
        if (observer != null) {
            observer.stopWatching();
        }
    }


    @Override
    public void onGenerateWeatherDataClicked(View view) {
        if (isLocationPermissionGranted()) {
            showLoading();
            locationManager.startLocationUpdates();
        } else {
            initLocationManager();
        }
    }

    @Override
    public void onLocationRetrieved(Location location) {
        currentLocation = location;
        // we don't need more location update. so we should stop it.
        locationManager.stopLocationUpdates();
        // get location data from server by lat/long
        weatherPhotoViewModel.setLocation(location);
    }

    private void setOverlayVisible(boolean visible) {
        binding.overlay.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void showLoading() {
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.loadingProgressBar.setVisibility(View.GONE);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                initLocationManager();
            } else {
                // permission denied
                PermissionManager.showApplicationSettingsDialog(getContext());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(TAG, "User agreed to make required location settings changes.");
                    if (locationManager != null)
                        locationManager.startLocationUpdates();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(TAG, "User chose not to make required location settings changes.");
                    hideLoading();
                    break;
                default:
                    break;
            }
        }
    }
}
