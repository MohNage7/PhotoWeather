package com.mohnage7.weather.ui.home.di.module;

import com.mohnage7.weather.data.db.WeatherDatabase;
import com.mohnage7.weather.ui.home.repository.HomeRepository;

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
