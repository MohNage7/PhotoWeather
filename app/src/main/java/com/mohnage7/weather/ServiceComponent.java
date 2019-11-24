package com.mohnage7.weather;


import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.network.RestApiService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ServiceModule.class})
public interface ServiceComponent {
    RestApiService getRestApiService();
    WeatherDatabase getWeatherDataBase();

    void inject(WeatherPhotoApplication weatherPhotoApplication);
}
