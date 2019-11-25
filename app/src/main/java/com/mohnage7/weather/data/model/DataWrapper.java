package com.mohnage7.weather.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataWrapper<T> {
    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public DataWrapper(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> DataWrapper<T> success(@NonNull T data) {
        return new DataWrapper<>(Status.SUCCESS, data, null);
    }

    public static <T> DataWrapper<T> error(@NonNull String msg, @Nullable T data) {
        return new DataWrapper<>(Status.ERROR, data, msg);
    }

    public static <T> DataWrapper<T> loading(@Nullable T data) {
        return new DataWrapper<>(Status.LOADING, data, null);
    }

    public enum Status {SUCCESS, ERROR, LOADING}

}
