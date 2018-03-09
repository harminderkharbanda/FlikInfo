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
 * Created by harminder on 05/03/18.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder> {
    private List<MovieTrailersData> movieTrailersData;
    private final TrailersAdapter.TrailerClickHandler trailerClickHandler;
    private final Context mContext;

    public TrailersAdapter(TrailerClickHandler clickHandler, Context context) {
        trailerClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForTrailerItem = R.layout.trailer_item;
        View view = inflater.inflate(layoutIdForTrailerItem, viewGroup, false);
        return new TrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersViewHolder holder, int position) {
        String currentKey = movieTrailersData.get(position).key;
        Picasso.with(mContext).load(FlikInfoConstants.URLConstants.YOUTUBE_THUMBNAIL_BASE_URL + currentKey + "/0.jpg").into(holder.trailerImageView);
    }

    @Override
    public int getItemCount() {
        if (null == movieTrailersData) return 0;
        return movieTrailersData.size();

    }

    public interface TrailerClickHandler {
        void onClick (MovieTrailersData movieTrailersData);
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView trailerImageView;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            trailerImageView = itemView.findViewById(R.id.iv_movie_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            trailerClickHandler.onClick(movieTrailersData.get(position));
        }
    }

    public void setTrailerData(List<MovieTrailersData> movieTrailersData) {
        this.movieTrailersData = movieTrailersData;
    }

}
