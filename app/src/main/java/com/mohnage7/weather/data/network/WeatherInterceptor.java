package com.mohnage7.weather.data.network;

import androidx.annotation.NonNull;

import com.mohnage7.weather.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherInterceptor implements Interceptor {

    private static final String API_KEY = "APPID";
    private static final String UNIT = "units";
    private static final String METRIC = "metric";


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();
        HttpUrl url = originalHttpUrl.newBuilder()
                .addQueryParameter(API_KEY, BuildConfig.API_KEY)
                .addQueryParameter(UNIT, METRIC)
                .build();

        Request.Builder requestBuilder = original.newBuilder().url(url);
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
