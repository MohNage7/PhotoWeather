package com.mohnage7.weather.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mohnage7.weather.data.model.WeatherModel;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WeatherDataDao {
    @Insert(onConflict = REPLACE)
    void insert(WeatherModel weatherInfo);

    @Query("SELECT * FROM weatherModel")
    LiveData<List<WeatherModel>> getAllWeatherData();

    @Query("SELECT * FROM weatherModel WHERE locationId = :id")
    LiveData<WeatherModel> getWeatherData(String id);
}
