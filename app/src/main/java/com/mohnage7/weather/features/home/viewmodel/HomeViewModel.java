package com.mohnage7.weather.features.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.mohnage7.weather.MoviesApplication;
import com.mohnage7.weather.base.DataWrapper;
import com.mohnage7.weather.model.Movie;
import com.mohnage7.weather.features.home.repository.HomeRepository;

import java.util.List;

import javax.inject.Inject;


public class HomeViewModel extends ViewModel {

    @Inject
    HomeRepository repository;

    private MutableLiveData<String> category = new MutableLiveData<>();
    private LiveData<DataWrapper<List<Movie>>> moviesList;

    public HomeViewModel() {
       // MoviesApplication.getInstance().getServiceComponent().inject(this);
        moviesList = Transformations.switchMap(category, category -> repository.getMovies(category));
    }

    public LiveData<DataWrapper<List<Movie>>> getMoviesList() {
        return moviesList;
    }

    public void setFilterMovieBy(String filter) {
        category.setValue(filter);
    }

}
