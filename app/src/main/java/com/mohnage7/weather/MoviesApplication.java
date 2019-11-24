package com.mohnage7.weather;

import android.app.Application;


public class MoviesApplication extends Application {

    private static MoviesApplication instance;
    ServiceComponent serviceComponent;

    public static MoviesApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDataComponent();
    }

    private void initDataComponent() {
        serviceComponent = DaggerServiceComponent.builder().
                serviceModule(new ServiceModule(this))
                .build();
        serviceComponent.inject(this);
    }

    public ServiceComponent getServiceComponent() {
        return serviceComponent;
    }
}
