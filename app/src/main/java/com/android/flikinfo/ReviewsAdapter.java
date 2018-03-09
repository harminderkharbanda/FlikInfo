package com.android.flikinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harminder on 06/03/18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<MovieReviewsData> reviewsData;
    private ReviewsClickHandler handler;

    public ReviewsAdapter(ReviewsClickHandler clickHandler) {
        handler = clickHandler;
    }

    public interface ReviewsClickHandler {
        void onClick (MovieReviewsData movieReviewsData);
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutId = R.layout.review_item;
        View v = inflater.inflate(layoutId, viewGroup, false);
        return new ReviewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        holder.reviewsTextView.setText(reviewsData.get(position).review);
        holder.authorTextView.setText("-" + reviewsData.get(position).author);
    }

    @Override
    public int getItemCount() {
        if (reviewsData == null) return 0;
        return reviewsData.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView reviewsTextView;
        private final TextView authorTextView;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            reviewsTextView = itemView.findViewById(R.id.tv_review);
            authorTextView = itemView.findViewById(R.id.tv_author);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            handler.onClick(reviewsData.get(position));
        }
    }

    public void setReviewsData(List<MovieReviewsData> movieReviewsData) {
        reviewsData = movieReviewsData;
    }
}
