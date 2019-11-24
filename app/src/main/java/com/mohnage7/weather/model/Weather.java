package com.mohnage7.weather.model;

import com.google.gson.annotations.Expose;

import static com.mohnage7.weather.BuildConfig.IMAGE_URL;


public class Weather {
    @Expose
    private String icon;
    @Expose
    private String main;


    private String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWeatherIconURL() {
        return new StringBuilder().
                append(IMAGE_URL).
                append(getIcon()).append("@2x").append(".png").toString();
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

}
