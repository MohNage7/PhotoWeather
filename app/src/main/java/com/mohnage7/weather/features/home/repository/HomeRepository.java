package com.mohnage7.weather.features.home.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.mohnage7.weather.network.ApiResponse;
import com.mohnage7.weather.network.RestApiService;
import com.mohnage7.weather.base.DataWrapper;
import com.mohnage7.weather.db.AppExecutors;
import com.mohnage7.weather.db.WeatherDao;
import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.model.Movie;
import com.mohnage7.weather.model.MoviesResponse;
import com.mohnage7.weather.network.NetworkBoundResource;
import com.mohnage7.weather.utils.RefreshRateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import static com.mohnage7.weather.utils.Constants.CACHE_TIMEOUT;

public class HomeRepository {

    private final AppExecutors appExecutors;
    private RestApiService apiService;
    private WeatherDao weatherDao;
    private RefreshRateLimiter refreshRateLimiter;

    @Inject
    public HomeRepository(RestApiService apiService, WeatherDatabase weatherDatabase, AppExecutors appExecutors) {
        this.apiService = apiService;
        this.appExecutors = appExecutors;
        weatherDao = weatherDatabase.getWeatherDao();
        refreshRateLimiter = new RefreshRateLimiter(TimeUnit.MINUTES, CACHE_TIMEOUT);
    }

    public LiveData<DataWrapper<List<Movie>>> getMovies(String category) {
        return new NetworkBoundResource<List<Movie>, MoviesResponse>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull MoviesResponse item) {
                addFilterToEveryMovie(item.getMovieList(), category);
               // weatherDao.insertAll(item.getMovieList());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Movie> data) {
                return data == null || data.isEmpty() || refreshRateLimiter.shouldFetch(category);
            }

            @NonNull
            @Override
            protected LiveData<List<Movie>> loadFromDb() {
                return null;
            //    return weatherDao.getAllMovies(category);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MoviesResponse>> createCall() {
                return null;
            }
        }.getAsLiveData();
    }

    private void addFilterToEveryMovie(List<Movie> movieList, String category) {
        for (Movie movie : movieList) {
            movie.setCategory(category);
        }
    }
}
