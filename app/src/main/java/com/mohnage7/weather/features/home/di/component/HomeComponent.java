package com.mohnage7.weather.features.home.di.component;


import com.mohnage7.weather.ServiceComponent;
import com.mohnage7.weather.features.home.di.module.HomeModule;
import com.mohnage7.weather.features.home.viewmodel.HomeViewModel;
import com.mohnage7.weather.utils.FragmentScoped;

import dagger.Component;

@FragmentScoped
@Component(dependencies = ServiceComponent.class, modules = {HomeModule.class})
public interface HomeComponent {
    void inject(HomeViewModel homeViewModel);
}
