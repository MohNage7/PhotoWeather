package com.mohnage7.weather.features.weatherphoto.di.component;


import com.mohnage7.weather.ServiceComponent;
import com.mohnage7.weather.features.weatherphoto.di.module.WeatherPhotoModule;
import com.mohnage7.weather.features.weatherphoto.viewmodel.WeatherPhotoViewModel;
import com.mohnage7.weather.utils.FragmentScoped;

import dagger.Component;

@FragmentScoped
@Component(dependencies = ServiceComponent.class, modules = {WeatherPhotoModule.class})
public interface WeatherPhotoComponent {
    void inject(WeatherPhotoViewModel weatherDataViewModel);
}
