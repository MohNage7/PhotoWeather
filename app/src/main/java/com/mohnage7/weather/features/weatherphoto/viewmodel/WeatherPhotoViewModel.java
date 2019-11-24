package com.mohnage7.weather.features.weatherphoto.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.mohnage7.weather.MoviesApplication;
import com.mohnage7.weather.features.weatherphoto.di.component.DaggerWeatherPhotoComponent;
import com.mohnage7.weather.features.weatherphoto.di.module.WeatherPhotoModule;
import com.mohnage7.weather.features.weatherphoto.repository.WeatherPhotoRepository;
import com.mohnage7.weather.model.DataWrapper;
import com.mohnage7.weather.model.WeatherModel;

import javax.inject.Inject;


public class WeatherPhotoViewModel extends ViewModel {

    @Inject
    WeatherPhotoRepository repository;

    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private LiveData<DataWrapper<WeatherModel>> weatherInfoWrapperLiveData;

    public WeatherPhotoViewModel() {
        DaggerWeatherPhotoComponent.builder()
                .serviceComponent(MoviesApplication.getInstance().getServiceComponent())
                .weatherPhotoModule(new WeatherPhotoModule(MoviesApplication.getInstance()))
                .build()
                .inject(this);


        weatherInfoWrapperLiveData = Transformations.switchMap(locationMutableLiveData, location -> repository.getWeatherData(location));

    }


    public LiveData<DataWrapper<WeatherModel>> getWeatherData() {
        return weatherInfoWrapperLiveData;
    }

    public void setLocation(Location location) {
        locationMutableLiveData.setValue(location);
    }

    public void saveWeatherPhoto(String photoPath) {
        repository.insertWeatherPhoto(photoPath);
    }
}
