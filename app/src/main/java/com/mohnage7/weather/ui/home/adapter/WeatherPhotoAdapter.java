package com.mohnage7.weather.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mohnage7.weather.R;
import com.mohnage7.weather.data.model.WeatherPhoto;
import com.mohnage7.weather.databinding.ItemWeatherPhotoBinding;
import com.mohnage7.weather.ui.weatherphoto.view.callback.OnWeatherPhotoClickListener;

import java.util.List;


public class WeatherPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<WeatherPhoto> weatherPhotosList;
    private OnWeatherPhotoClickListener onWeatherPhotoClickListener;
    private ItemWeatherPhotoBinding binding;

    public WeatherPhotoAdapter(List<WeatherPhoto> weatherPhotosList, OnWeatherPhotoClickListener onWeatherPhotoClickListener) {
        this.weatherPhotosList = weatherPhotosList;
        this.onWeatherPhotoClickListener = onWeatherPhotoClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_weather_photo, parent, false);
        binding.setOnClickListener(onWeatherPhotoClickListener);
        return new WeatherPhotoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WeatherPhoto weatherPhoto = weatherPhotosList.get(position);
        binding.setWeatherPhoto(weatherPhoto);
    }

    @Override
    public int getItemCount() {
        if (weatherPhotosList != null && !weatherPhotosList.isEmpty()) {
            return weatherPhotosList.size();
        } else {
            return 0;
        }
    }


    protected class WeatherPhotoViewHolder extends RecyclerView.ViewHolder {
        WeatherPhotoViewHolder(@NonNull ItemWeatherPhotoBinding binding) {
            super(binding.getRoot());
        }
    }
}
