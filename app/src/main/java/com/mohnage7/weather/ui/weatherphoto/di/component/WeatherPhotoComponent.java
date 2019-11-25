package com.mohnage7.weather.ui.weatherphoto.di.component;


import com.mohnage7.weather.di.ServiceComponent;
import com.mohnage7.weather.ui.weatherphoto.di.module.WeatherPhotoModule;
import com.mohnage7.weather.ui.weatherphoto.viewmodel.WeatherPhotoViewModel;
import com.mohnage7.weather.utils.FragmentScoped;

import dagger.Component;

@FragmentScoped
@Component(dependencies = ServiceComponent.class, modules = {WeatherPhotoModule.class})
public interface WeatherPhotoComponent {
    void inject(WeatherPhotoViewModel weatherDataViewModel);
}
