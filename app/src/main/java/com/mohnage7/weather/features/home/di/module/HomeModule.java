package com.mohnage7.weather.features.home.di.module;

import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.features.home.repository.HomeRepository;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    public HomeModule() {
    }


    @Provides
    HomeRepository providesHomeRepository( WeatherDatabase weatherDatabase) {
        return new HomeRepository(weatherDatabase);
    }

}
