package com.mohnage7.weather.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.mohnage7.weather.model.WeatherPhoto;

import java.util.List;

import io.reactivex.Observable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WeatherPhotoDao {
    @Insert(onConflict = REPLACE)
    void insert(WeatherPhoto weatherPhoto);

    @Query("SELECT * FROM weatherPhoto")
    Observable<List<WeatherPhoto>> getWeatherPhotoList();

}
