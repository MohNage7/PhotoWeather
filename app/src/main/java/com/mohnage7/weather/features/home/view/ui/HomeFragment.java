package com.mohnage7.weather.features.home.view.ui;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mohnage7.weather.OnFragmentInteractionListener;
import com.mohnage7.weather.R;
import com.mohnage7.weather.databinding.FragmentHomeBinding;
import com.mohnage7.weather.features.home.adapter.WeatherPhotoAdapter;
import com.mohnage7.weather.features.home.view.callback.HomeHandler;
import com.mohnage7.weather.features.home.viewmodel.HomeViewModel;
import com.mohnage7.weather.features.weatherphoto.view.callback.OnWeatherPhotoClickListener;
import com.mohnage7.weather.model.WeatherPhoto;
import com.mohnage7.weather.utils.PermissionManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.mohnage7.weather.features.share.ui.ShareFragment.WEATHER_PHOTO_EXTRA;
import static com.mohnage7.weather.utils.PermissionManager.MULTIPLE_PERMISSION_REQUEST_CODE;

public class HomeFragment extends Fragment implements OnWeatherPhotoClickListener, HomeHandler {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private OnFragmentInteractionListener mListener;
    private Disposable disposable;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        observeWeatherPhotoChanges();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void observeWeatherPhotoChanges() {
        showLoadingLayout();
        disposable = homeViewModel.getWeatherPhotoList().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherPhotos -> {
                    hideLoadingLayout();
                    if (!weatherPhotos.isEmpty()) {
                        setupRecycler(weatherPhotos);
                    } else {
                        showEmptyLayout();
                    }
                }, e -> handleWeatherPhotoListError(e.getMessage()));
    }

    private void showEmptyLayout() {
        binding.emptyLayout.noDataLayout.setVisibility(View.VISIBLE);
        binding.weatherRecyclerView.setVisibility(View.GONE);
    }

    private void showLoadingLayout() {
        binding.weatherRecyclerView.setVisibility(View.GONE);
        binding.emptyLayout.noDataLayout.setVisibility(View.GONE);
        binding.loadingLayout.shimmerFrameLayout.startShimmer();
        binding.loadingLayout.shimmerFrameLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoadingLayout() {
        binding.loadingLayout.shimmerFrameLayout.stopShimmer();
        binding.loadingLayout.shimmerFrameLayout.setVisibility(View.GONE);
    }


    private void setDataViewsVisibility(boolean dataAvailable) {
        if (dataAvailable) {
            binding.weatherRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyLayout.noDataLayout.setVisibility(View.GONE);
        } else {
            binding.weatherRecyclerView.setVisibility(View.GONE);
            binding.emptyLayout.noDataLayout.setVisibility(View.VISIBLE);
        }
    }


    private void handleWeatherPhotoListError(String message) {
        setDataViewsVisibility(false);
        binding.emptyLayout.noDataTxtView.setText(message);
    }


    private void setupRecycler(List<WeatherPhoto> weatherPhotosList) {
        setDataViewsVisibility(true);
        WeatherPhotoAdapter adapter = new WeatherPhotoAdapter(weatherPhotosList, this);
        binding.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.weatherRecyclerView.setAdapter(adapter);
        hideLoadingLayout();
    }


    @Override
    public void onWeatherPhotoClick(String photoPath) {
        Bundle bundle = new Bundle();
        bundle.putString(WEATHER_PHOTO_EXTRA, photoPath);
        mListener.navigate(R.id.action_homeFragment_to_shareFragment, bundle);
    }


    @Override
    public void onNewPhotoClicked() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (PermissionManager.isAllPermissionGranted(this, permissions)) {
            mListener.navigate(R.id.action_homeFragment_to_cameraFragment, null);
        } else {
            PermissionManager.checkForPermissions(this, permissions);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted.
                onNewPhotoClicked();
            } else {
                // permission denied
                PermissionManager.showApplicationSettingsDialog(getContext());
            }
        }
    }
}
