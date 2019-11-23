package com.mohnage7.weather.base;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("status_code")
    private int mStatusCode;
    @SerializedName("status_message")
    private String mStatusMessage;
    @SerializedName("success")
    private Boolean mSuccess;

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        mStatusCode = statusCode;
    }

    public String getStatusMessage() {
        return mStatusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        mStatusMessage = statusMessage;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

}
