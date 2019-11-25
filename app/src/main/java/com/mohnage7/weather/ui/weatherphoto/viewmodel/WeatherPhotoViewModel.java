package com.mohnage7.weather.ui.weatherphoto.viewmodel;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.mohnage7.weather.WeatherPhotoApplication;
import com.mohnage7.weather.data.model.DataWrapper;
import com.mohnage7.weather.data.model.WeatherModel;
import com.mohnage7.weather.ui.weatherphoto.di.component.DaggerWeatherPhotoComponent;
import com.mohnage7.weather.ui.weatherphoto.di.module.WeatherPhotoModule;
import com.mohnage7.weather.ui.weatherphoto.repository.WeatherPhotoRepository;

import javax.inject.Inject;


public class WeatherPhotoViewModel extends ViewModel {

    @Inject
    WeatherPhotoRepository repository;

    private MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();
    private LiveData<DataWrapper<WeatherModel>> weatherInfoWrapperLiveData;

    public WeatherPhotoViewModel() {
        DaggerWeatherPhotoComponent.builder()
                .serviceComponent(WeatherPhotoApplication.getInstance().getServiceComponent())
                .weatherPhotoModule(new WeatherPhotoModule(WeatherPhotoApplication.getInstance()))
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
