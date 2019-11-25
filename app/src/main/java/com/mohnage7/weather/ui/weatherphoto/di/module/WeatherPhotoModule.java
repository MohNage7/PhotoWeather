package com.mohnage7.weather.ui.weatherphoto.di.module;

import android.app.Application;

import com.mohnage7.weather.data.db.AppExecutors;
import com.mohnage7.weather.data.db.WeatherDatabase;
import com.mohnage7.weather.data.network.RestApiEndPoints;
import com.mohnage7.weather.ui.weatherphoto.repository.WeatherPhotoRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class WeatherPhotoModule {
    private Application application;

    public WeatherPhotoModule(Application application) {
        this.application = application;
    }


    @Provides
    WeatherPhotoRepository providesWeatherPhotoRepository(RestApiEndPoints apiService, WeatherDatabase weatherDatabase) {
        return new WeatherPhotoRepository(apiService,weatherDatabase, AppExecutors.getInstance());
    }

}
