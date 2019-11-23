package com.mohnage7.weather.network;


import java.io.IOException;

import retrofit2.Response;


/**
 * Generic class for handling responses from Retrofit
 *
 * @param <T>
 */
public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error) {
        return new ApiErrorResponse<>(error.getMessage().equals("") ? error.getMessage() : "Unknown error\nCheck network connection");
    }

    public ApiResponse<T> create(Response<T> response) {

        if (response.isSuccessful()) {
            T body = response.body();
            if (body == null || response.code() == 204) { // 204 is empty response
                return new ApiEmptyResponse<>();
            } else {
                return new ApiSuccessResponse<>(body);
            }
        } else {
            String errorMsg = "";
            try {
                errorMsg = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
                errorMsg = response.message();
            }
            return new ApiErrorResponse<>(errorMsg);
        }
    }

    /**
     * Generic success response from api
     *
     * @param <T>
     */
    public class ApiSuccessResponse<T> extends ApiResponse<T> {

        private T body;

        ApiSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

    }

    /**
     * Generic Error response from API
     *
     * @param <T>
     */
    public class ApiErrorResponse<T> extends ApiResponse<T> {

        private String errorMessage;

        ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }


    /**
     * separate class for HTTP 204 resposes so that we can make ApiSuccessResponse's body non-null.
     */
    public class ApiEmptyResponse<T> extends ApiResponse<T> {
    }

}





















