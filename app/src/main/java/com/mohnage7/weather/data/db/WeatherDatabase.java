package com.mohnage7.weather.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mohnage7.weather.data.model.WeatherModel;
import com.mohnage7.weather.data.model.WeatherPhoto;

@Database(entities = {WeatherModel.class,WeatherPhoto.class}, version = 1, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {

    private static final String DATA_BASE_NAME = "weather_db";
    private static WeatherDatabase INSTANCE;

    public static synchronized WeatherDatabase getDatabaseInstance(Context context) {
        // insure that no other reference is created on different threads.
        if (INSTANCE == null) {
            // create our one and only db object.
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    // fallbackToDestructiveMigration is a migration strategy that destroy and re-creating existing db
                    // fallbackToDestructiveMigration is only used for small applications like we are implementing now
                    // for real projects we need to implement non-destructive migration strategy.
                    WeatherDatabase.class, DATA_BASE_NAME).fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public abstract WeatherDataDao getWeatherDataDao();
    public abstract WeatherPhotoDao getWeatherPhotoDao();

}
