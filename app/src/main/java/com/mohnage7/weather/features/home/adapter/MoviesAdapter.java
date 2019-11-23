package com.mohnage7.weather.features.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohnage7.weather.R;
import com.mohnage7.weather.base.BaseViewHolder;
import com.mohnage7.weather.model.Movie;
import com.mohnage7.weather.features.weatherphoto.view.callback.OnMovieClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;



public class MoviesAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<Movie> movieList;
    private OnMovieClickListener onMovieClickListener;

    public MoviesAdapter(List<Movie> moviesList, OnMovieClickListener onMovieClickListener) {
        this.movieList = moviesList;
        this.onMovieClickListener = onMovieClickListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        view.getLayoutParams().height = (int) (parent.getWidth() / 3 *
                1.5);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bindViews(position);
    }

    @Override
    public int getItemCount() {
        if (movieList != null && !movieList.isEmpty()) {
            return movieList.size();
        } else {
            return 0;
        }
    }


    protected class MoviesViewHolder extends BaseViewHolder {
        //@BindView(R.id.movie_img_view)
        ImageView movieImageView;
      //  @BindView(R.id.movie_title_txt_view)
        TextView movieTitle;


        MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bindViews(int position) {
            super.bindViews(position);
            Movie movie = movieList.get(position);
            movieTitle.setText(movie.getTitle());
            displayArticleImage(movie.getPosterPath());
            itemView.setOnClickListener(v -> onMovieClickListener.onMovieClick(movie, itemView));
        }

        private void displayArticleImage(String imageUrl) {
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).error(R.drawable.placeholder).into(movieImageView);
            } else {
                Picasso.get().load(R.drawable.placeholder).into(movieImageView);
            }
        }


        @Override
        public void clear() {
            movieTitle.setText("");
            movieImageView.setImageDrawable(null);
        }
    }
}
