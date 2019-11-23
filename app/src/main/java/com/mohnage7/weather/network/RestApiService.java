package com.mohnage7.weather.network;


import androidx.lifecycle.LiveData;

import com.mohnage7.weather.model.MoviesResponse;
import com.mohnage7.weather.model.WeatherInfo;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiService {

    @GET("data/2.5/weather")
    LiveData<ApiResponse<WeatherInfo>> getWeatherData(@Query("lat") double latitude, @Query("lon") double longitude);

}
