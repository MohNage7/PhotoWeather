package com.mohnage7.weather.features.home.viewmodel;

import androidx.lifecycle.ViewModel;

import com.mohnage7.weather.WeatherPhotoApplication;
import com.mohnage7.weather.features.home.di.component.DaggerHomeComponent;
import com.mohnage7.weather.features.home.di.module.HomeModule;
import com.mohnage7.weather.features.home.repository.HomeRepository;
import com.mohnage7.weather.model.WeatherPhoto;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;


public class HomeViewModel extends ViewModel {

    @Inject
    HomeRepository repository;


    public HomeViewModel() {
        DaggerHomeComponent.builder()
                .serviceComponent(WeatherPhotoApplication.getInstance().getServiceComponent())
                .homeModule(new HomeModule())
                .build()
                .inject(this);
     }

    public Observable<List<WeatherPhoto>> getWeatherPhotoList() {
        return repository.getWeatherPhotoList();
    }


}
