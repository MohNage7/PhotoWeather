package com.mohnage7.weather.model;

import com.google.gson.annotations.Expose;


public class Sys {

    @Expose
    private String country;
    @Expose
    private Long id;
    @Expose
    private Long sunrise;
    @Expose
    private Long sunset;
    @Expose
    private Long type;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSunrise() {
        return sunrise;
    }

    public void setSunrise(Long sunrise) {
        this.sunrise = sunrise;
    }

    public Long getSunset() {
        return sunset;
    }

    public void setSunset(Long sunset) {
        this.sunset = sunset;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

}
