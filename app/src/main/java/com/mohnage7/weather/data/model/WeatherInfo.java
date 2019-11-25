package com.mohnage7.weather.data.model;

import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.List;


public class WeatherInfo {
    @PrimaryKey
    @Expose
    private String locationId;
    @Expose
    private Main main;
    @Expose
    private String name;
    @Expose
    private Sys sys;
    @Expose
    private List<Weather> weather;


    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }


    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }


}
