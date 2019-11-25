package com.mohnage7.weather.di;


import com.mohnage7.weather.WeatherPhotoApplication;
import com.mohnage7.weather.data.db.WeatherDatabase;
import com.mohnage7.weather.data.network.RestApiEndPoints;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ServiceModule.class})
public interface ServiceComponent {
    RestApiEndPoints getRestApiService();
    WeatherDatabase getWeatherDataBase();

    void inject(WeatherPhotoApplication weatherPhotoApplication);
}
