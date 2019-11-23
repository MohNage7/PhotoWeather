package com.mohnage7.weather.model;

import com.google.gson.annotations.Expose;

import static com.mohnage7.weather.BuildConfig.BASE_URL;
import static com.mohnage7.weather.BuildConfig.IMAGE_URL;


public class Weather {

    @Expose
    private String description;
    @Expose
    private String icon;
    @Expose
    private Long id;
    @Expose
    private String main;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWeatherIconURL() {
        return new StringBuilder().
                append(IMAGE_URL).
                append(icon).append("@2x").append(".png").toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

}
