package com.mohnage7.weather.model;

import com.google.gson.annotations.Expose;


public class Sys {

    @Expose
    private String country;

    public String getCountry() {
        return country;
    }

}
