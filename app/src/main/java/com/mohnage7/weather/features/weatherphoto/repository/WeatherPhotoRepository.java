package com.mohnage7.weather.features.weatherphoto.repository;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.mohnage7.weather.base.DataWrapper;
import com.mohnage7.weather.db.AppExecutors;
import com.mohnage7.weather.db.WeatherDao;
import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.model.WeatherInfo;
import com.mohnage7.weather.model.WeatherModel;
import com.mohnage7.weather.network.ApiResponse;
import com.mohnage7.weather.network.NetworkBoundResource;
import com.mohnage7.weather.network.RestApiService;
import com.mohnage7.weather.utils.RefreshRateLimiter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static com.mohnage7.weather.utils.Constants.CACHE_TIMEOUT;

public class WeatherPhotoRepository {

    private final AppExecutors appExecutors;
    private RestApiService apiService;
    private WeatherDao weatherDao;
    private RefreshRateLimiter refreshRateLimiter;


    @Inject
    public WeatherPhotoRepository(RestApiService apiService, WeatherDatabase weatherDatabase, AppExecutors appExecutors) {
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        weatherDao = weatherDatabase.getWeatherDao();
        refreshRateLimiter = new RefreshRateLimiter(TimeUnit.MINUTES, CACHE_TIMEOUT);
    }

    /**
     * Depend on cache if there's a request from same lat/long in 2 mints
     * @param location use it's lat/long to get weather data from OpenWeather API
     * @return WeatherModel attached with status to the view.
     */
    public LiveData<DataWrapper<WeatherModel>> getWeatherData(Location location) {
        String locationId = generateLocationId(location);
        return new NetworkBoundResource<WeatherModel, WeatherInfo>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull WeatherInfo item) {
                WeatherModel weatherModel = getWeatherModel(item, locationId);
                weatherDao.insert(weatherModel);
            }

            @Override
            protected boolean shouldFetch(@Nullable WeatherModel data) {
                return data == null || refreshRateLimiter.shouldFetch(locationId);
            }

            @NonNull
            @Override
            protected LiveData<WeatherModel> loadFromDb() {
                return weatherDao.getWeatherData(locationId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<WeatherInfo>> createCall() {
                return apiService.getWeatherData(location.getLatitude(), location.getLongitude());
            }
        }.getAsLiveData();
    }

    private WeatherModel getWeatherModel(WeatherInfo weatherInfo, String locationId) {
        WeatherModel weatherModel = new WeatherModel();
        weatherModel.setLocationId(locationId);
        weatherModel.setCountryName(weatherInfo.getSys().getCountry());
        weatherModel.setName(weatherInfo.getName());
        weatherModel.setTempStatus(weatherInfo.getWeather().get(0).getMain());
        weatherModel.setTemp(weatherInfo.getMain().getTemp());
        weatherModel.setMaxTemp(weatherInfo.getMain().getTempMax());
        weatherModel.setMinTemp(weatherInfo.getMain().getTempMin());
        weatherModel.setTempIconURL(weatherInfo.getWeather().get(0).getWeatherIconURL());
        return weatherModel;
    }

    private String generateLocationId(Location location) {
        return String.valueOf(location.getLatitude() + location.getLongitude());
    }

    public void insertImage(String photoPath) {
        //weatherDao.insert();
    }
}
