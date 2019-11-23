package com.mohnage7.weather.di.component;


import com.mohnage7.weather.MoviesApplication;
import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.di.module.ServiceModule;
import com.mohnage7.weather.network.RestApiService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ServiceModule.class})
public interface ServiceComponent {
    RestApiService getRestApiService();
    WeatherDatabase getWeatherDataBase();

    void inject(MoviesApplication moviesApplication);
}
