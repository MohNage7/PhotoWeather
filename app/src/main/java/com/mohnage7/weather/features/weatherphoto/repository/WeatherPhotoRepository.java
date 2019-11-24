package com.mohnage7.weather.features.weatherphoto.repository;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.mohnage7.weather.db.AppExecutors;
import com.mohnage7.weather.db.WeatherDataDao;
import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.db.WeatherPhotoDao;
import com.mohnage7.weather.model.DataWrapper;
import com.mohnage7.weather.model.WeatherInfo;
import com.mohnage7.weather.model.WeatherModel;
import com.mohnage7.weather.model.WeatherPhoto;
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
    private WeatherDataDao weatherDataDao;
    private WeatherPhotoDao weatherPhotoDao;
    private RefreshRateLimiter refreshRateLimiter;
    private static final String IMAGE_FORMAT = ".jpg";


    @Inject
    public WeatherPhotoRepository(RestApiService apiService, WeatherDatabase weatherDatabase, AppExecutors appExecutors) {
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        weatherDataDao = weatherDatabase.getWeatherDataDao();
        weatherPhotoDao = weatherDatabase.getWeatherPhotoDao();
        refreshRateLimiter = new RefreshRateLimiter(TimeUnit.MINUTES, CACHE_TIMEOUT);
    }

    /**
     * Depend on cache if there's a request from same lat/long in 2 mints
     *
     * @param location use it's lat/long to get weather data from OpenWeather API
     * @return WeatherModel attached with status to the view.
     */
    public LiveData<DataWrapper<WeatherModel>> getWeatherData(Location location) {
        String locationId = generateLocationId(location);
        return new NetworkBoundResource<WeatherModel, WeatherInfo>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull WeatherInfo item) {
                WeatherModel weatherModel = getWeatherModel(item, locationId);
                weatherDataDao.insert(weatherModel);
            }

            @Override
            protected boolean shouldFetch(@Nullable WeatherModel data) {
                return data == null || refreshRateLimiter.shouldFetch(locationId);
            }

            @NonNull
            @Override
            protected LiveData<WeatherModel> loadFromDb() {
                return weatherDataDao.getWeatherData(locationId);
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

    public void insertWeatherPhoto(String photoPath) {
        appExecutors.diskIO().execute(() -> {
            String photoName = getPhotoName(photoPath);
            WeatherPhoto weatherPhoto = new WeatherPhoto(photoName, photoPath);
            weatherPhotoDao.insert(weatherPhoto);
        });
    }

    private String getPhotoName(String photoPath) {
        String name = photoPath.replace(IMAGE_FORMAT, "");
        String[] arr = name.split("_");
        name = arr[1] + "_" + arr[2];
        return name;
    }
}
