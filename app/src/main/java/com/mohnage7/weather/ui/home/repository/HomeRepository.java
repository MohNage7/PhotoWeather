package com.mohnage7.weather.ui.home.repository;

import com.mohnage7.weather.data.db.WeatherDatabase;
import com.mohnage7.weather.data.db.WeatherPhotoDao;
import com.mohnage7.weather.data.model.WeatherPhoto;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class HomeRepository {

    private WeatherPhotoDao weatherDataDao;

    @Inject
    public HomeRepository(WeatherDatabase weatherDatabase) {
        weatherDataDao = weatherDatabase.getWeatherPhotoDao();
    }

    public Observable<List<WeatherPhoto>> getWeatherPhotoList() {
        return weatherDataDao.getWeatherPhotoList();
    }
}
