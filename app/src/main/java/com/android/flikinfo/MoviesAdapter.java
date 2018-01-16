package com.android.flikinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by harminder on 01/01/18
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<MovieData> movieData;
    private final MovieThumbnailClickHandler movieThumbnailClickhandler;
    private final Context mContext;


    public MoviesAdapter(MovieThumbnailClickHandler clickHandler, Context context) {
        movieThumbnailClickhandler = clickHandler;
        mContext = context;
    }

    public interface MovieThumbnailClickHandler {
        void onClick (MovieData movieData);
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mImageThumbnail;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            mImageThumbnail = itemView.findViewById(R.id.movieThumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            movieThumbnailClickhandler.onClick(movieData.get(adapterPosition));
        }
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForMovieItem = R.layout.movie_item;
        View view = inflater.inflate(layoutIdForMovieItem, viewGroup, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        String currentImagePath = movieData.get(position).imagePath;
        Picasso.with(mContext).load(FlikInfoConstants.URLConstants.IMAGE_BASE_URL + '/' + currentImagePath).into(holder.mImageThumbnail);
    }

    @Override
    public int getItemCount() {
        if (null == movieData) return 0;
        return movieData.size();
    }

    public void setMovieData(List<MovieData> movieData) {
        this.movieData = movieData;
    }

}
