package com.mohnage7.weather.features.weatherphoto.view.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
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


public class WeatherPhotoFragment extends Fragment implements WeatherPhotoHandler, LocationManagerInteraction {

    public static final String PHOTO_EXTRA = "photo";
    private static final String IMAGE_TYPE = "image/*";
    private static final String TAG = WeatherPhotoFragment.class.getSimpleName();
    private String photoPath;
    private FragmentWeatherPhotoBinding binding;
    private LocationManager locationManager;
    private Location currentLocation;
    private WeatherPhotoViewModel weatherPhotoViewModel;
    private Activity activity;
    private OnFragmentInteractionListener mListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(PHOTO_EXTRA)) {
            photoPath = bundle.getString(PHOTO_EXTRA);
        }
        activity = getActivity();
        listenToWeatherDataChanges();
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
                    hideLoading();
                    generateWeatherDataOverTheImage(dataWrapper.data);
                    setShareButtonVisible(true);
                    changeToolbarTitle();
                    break;
                default:
                    break;
            }
        });
    }

    private void generateWeatherDataOverTheImage(WeatherModel weatherModel) {
        binding.locationNameTv.setText(String.format("%s, %s", weatherModel.getName(), weatherModel.getCountryName()));
        binding.tempStatusTv.setText(weatherModel.getTempStatus());
        binding.tempTv.setText(String.format("%s°", String.valueOf(weatherModel.getTemp())));
        binding.minMaxTv.setText(String.format("%s ° / %s °", weatherModel.getMaxTemp(), weatherModel.getMinTemp()));
        Picasso.get().load(weatherModel.getTempIconURL()).into(binding.temperatureStatusIv);
        Bitmap bitmap = convertViewToBitmap(binding.weatherPhotoLayout);
        replaceOriginalBitmapWithGeneratedBitmap(bitmap);
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
        }
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
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
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
        changeToolbarTitle();
        setShareButtonVisible(currentLocation != null);
        if (PermissionManager.checkLocationPermission(this) && currentLocation == null) {
            locationManager = new LocationManager(activity, this);
        }
    }

    private void changeToolbarTitle() {
        mListener.setToolbarTitle(currentLocation == null ? getString(R.string.generate_photo) : getString(R.string.share_photo));
    }


    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.stopLocationUpdates();
        }
    }

    @Override
    public void onShareClicked(View view) {
        shareImage(photoPath);
    }

    private void shareImage(String photoPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(IMAGE_TYPE);
        File photoFile = new File(photoPath);
        Uri myPhotoFileUri = FileProvider.getUriForFile(activity,
                activity.getApplicationContext().getPackageName()
                        + ".provider", photoFile);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri);
        startActivity(Intent.createChooser(intent, "Share with..."));
    }

    @Override
    public void onGenerateWeatherDataClicked(View view) {
        if (PermissionManager.checkLocationPermission(this)) {
            showLoading();
            locationManager.startLocationUpdates();
        }
    }

    @Override
    public void onLocationRetrieved(Location location) {
        currentLocation = location;
        hideLoading();
        // we don't need more location update. so we should stop it.
        locationManager.stopLocationUpdates();
        // get location data from server by lat/long
        weatherPhotoViewModel.setLocation(location);
    }

    private void setShareButtonVisible(boolean visible) {
        if (visible) {
            binding.shareWeatherPhotoBtn.setVisibility(View.VISIBLE);
            binding.generateWeatherDataBtn.setVisibility(View.INVISIBLE);
        } else {
            binding.shareWeatherPhotoBtn.setVisibility(View.INVISIBLE);
            binding.generateWeatherDataBtn.setVisibility(View.VISIBLE);
        }
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


}
