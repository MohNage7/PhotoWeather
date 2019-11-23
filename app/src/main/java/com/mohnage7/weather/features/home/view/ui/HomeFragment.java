package com.mohnage7.weather.features.home.view.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mohnage7.weather.OnFragmentInteractionListener;
import com.mohnage7.weather.R;
import com.mohnage7.weather.databinding.FragmentHomeBinding;
import com.mohnage7.weather.features.home.adapter.MoviesAdapter;
import com.mohnage7.weather.features.home.view.callback.HomeHandler;
import com.mohnage7.weather.features.home.viewmodel.HomeViewModel;
import com.mohnage7.weather.features.weatherphoto.view.callback.OnMovieClickListener;
import com.mohnage7.weather.model.Movie;
import com.mohnage7.weather.utils.PermissionManager;

import java.util.List;

public class HomeFragment extends Fragment implements OnMovieClickListener, HomeHandler {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // init view model
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void getMovies(String category) {
        homeViewModel.getMoviesList().observe(this, dataWrapper -> {
            switch (dataWrapper.status) {
                case LOADING:
                    showLoadingLayout();
                    break;
                case SUCCESS:
                    hideLoadingLayout();
                    setupMoviesRecycler(dataWrapper.data);
                    break;
                case ERROR:
                    hideLoadingLayout();
                    handleMoviesListError(dataWrapper.message);
                    break;
                default:
                    break;
            }
        });
        homeViewModel.setFilterMovieBy(category);
    }

    private void showLoadingLayout() {
        binding.weatherRecyclerView.setVisibility(View.GONE);
        binding.noDataLayout.setVisibility(View.GONE);
    }

    private void hideLoadingLayout() {
        binding.weatherRecyclerView.setVisibility(View.VISIBLE);
    }


    private void setDataViewsVisibility(boolean dataAvailable) {
        if (dataAvailable) {
            binding.weatherRecyclerView.setVisibility(View.VISIBLE);
            binding.noDataLayout.setVisibility(View.GONE);
        } else {
            binding.weatherRecyclerView.setVisibility(View.GONE);
            binding.noDataLayout.setVisibility(View.VISIBLE);
        }
    }


    private void handleMoviesListError(String message) {
        setDataViewsVisibility(false);
        binding.noDataTxtView.setText(message);
    }


    private void setupMoviesRecycler(List<Movie> moviesList) {
        hideLoadingLayout();
        setDataViewsVisibility(true);
        MoviesAdapter adapter = new MoviesAdapter(moviesList, this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        binding.weatherRecyclerView.setLayoutManager(layoutManager);
        binding.weatherRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onMovieClick(Movie movie, View view) {
//        Intent intent = new Intent(this, MovieDetailsActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(MOVIE_EXTRA, movie);
//        intent.putExtras(bundle);
//        startActivity(intent);
//        overridePendingTransition(R.anim.anim_slide_up, R.anim.no_animation);
    }


    @Override
    public void onNewPhotoClicked(View view) {
        PermissionManager.checkCameraPermission(this);
        Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_cameraFragment, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.setToolbarTitle(getString(R.string.weather_photo));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
