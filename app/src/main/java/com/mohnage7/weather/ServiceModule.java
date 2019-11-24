package com.mohnage7.weather;

import android.app.Application;

import com.mohnage7.weather.db.WeatherDatabase;
import com.mohnage7.weather.network.RestApiService;
import com.mohnage7.weather.network.WeatherInterceptor;
import com.mohnage7.weather.utils.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mohnage7.weather.BuildConfig.BASE_URL;
import static com.mohnage7.weather.utils.Constants.CONNECTION_TIMEOUT;
import static com.mohnage7.weather.utils.Constants.READ_TIMEOUT;
import static com.mohnage7.weather.utils.Constants.WRITE_TIMEOUT;

@Module
public class ServiceModule {
    private Application application;

    public ServiceModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    RestApiService provideRestService() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // add request time out
        builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // Add logging into
        builder.interceptors().add(httpLoggingInterceptor);
        builder.interceptors().add(new WeatherInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                //converts Retrofit response into Observable
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
        return retrofit.create(RestApiService.class);
    }

    @Provides
    @Singleton
    WeatherDatabase providesDatabase(){
        return WeatherDatabase.getDatabaseInstance(application);
    }
}
