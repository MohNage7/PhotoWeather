package com.mohnage7.weather.data.network;


import androidx.lifecycle.LiveData;

import com.mohnage7.weather.data.model.WeatherInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApiEndPoints {

    @GET("data/2.5/weather")
    LiveData<ApiResponse<WeatherInfo>> getWeatherData(@Query("lat") double latitude, @Query("lon") double longitude);

}
