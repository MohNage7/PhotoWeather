package com.mohnage7.weather.features.weatherphoto.di.module;

import android.app.Application;

import com.mohnage7.weather.db.AppExecutors;
import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.features.weatherphoto.repository.WeatherPhotoRepository;
import com.mohnage7.weather.network.RestApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WeatherPhotoModule {
    private Application application;

    public WeatherPhotoModule(Application application) {
        this.application = application;
    }


    @Provides
    WeatherPhotoRepository providesWeatherPhotoRepository(RestApiService apiService,WeatherDatabase weatherDatabase) {
        return new WeatherPhotoRepository(apiService,weatherDatabase, AppExecutors.getInstance());
    }

}
