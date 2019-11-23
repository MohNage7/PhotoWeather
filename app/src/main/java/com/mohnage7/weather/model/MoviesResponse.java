package com.mohnage7.weather.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mohnage7.weather.base.BaseResponse;

import java.util.List;

/**
 * Created by mohnage7 on 3/1/2018.
 */

public class MoviesResponse extends BaseResponse {
    @SerializedName("results")
    @Expose
    private List<Movie> movieList = null;

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

}
