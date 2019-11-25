package com.mohnage7.weather.data.model;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.mohnage7.weather.R;
import com.squareup.picasso.Picasso;

import java.io.File;

@Entity(tableName = "weatherPhoto")
public class WeatherPhoto {
    String name;
    @PrimaryKey
    @NonNull
    String photoPath;

    public WeatherPhoto(String name, @NonNull String photoPath) {
        this.name = name;
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @BindingAdapter("displayPhoto")
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.get().
                load(new File(imageUrl)).
                error(R.drawable.placeholder).
                into(view);
    }
}
