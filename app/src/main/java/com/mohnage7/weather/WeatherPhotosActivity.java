package com.mohnage7.weather;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.mohnage7.weather.databinding.ActivityWeatherBinding;

public class WeatherPhotosActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    ActivityWeatherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather);
        setSupportActionBar(binding.toolbar);
        // getWeatherData("");
        setupNavigationComponent();
    }

    /**
     * This method defines the navigation between the fragment that is hosted on nav_hos_fragment
     * Using the new Navigation Controller component.
     */
    private void setupNavigationComponent() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Navigation.findNavController(this,R.id.nav_host_fragment).navigateUp();
        return true;
    }


    @Override
    public void setToolbarTitle(String title) {
        binding.toolbar.setTitle(title);
    }

    @Override
    public void navigate(int resId, @Nullable Bundle bundle) {
        Navigation.findNavController(this,R.id.nav_host_fragment).
                navigate(resId,bundle);
    }
}
