package com.android.flikinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView movieTitle;
        ImageView moviePoster;
        TextView movieOverview;
        TextView movieRating;
        TextView movieReleaseDate;

        movieTitle = findViewById(R.id.tv_movie_title);
        moviePoster = findViewById(R.id.iv_movie_image);
        movieOverview = findViewById(R.id.tv_movie_overview);
        movieRating = findViewById(R.id.tv_movie_rating);
        movieReleaseDate = findViewById(R.id.tv_movie_release_date);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_TITLE)) {
                movieTitle.setText(intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_TITLE).toUpperCase());
            }
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_POSTER_PATH)) {
                Picasso.with(this).load(FlikInfoConstants.URLConstants.IMAGE_BASE_URL + intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_POSTER_PATH)).into(moviePoster);
            }
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_OVERVIEW)) {
                movieOverview.setText(intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_OVERVIEW));

            }
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RATING)) {
                String rating = "Rating: " + "<b> " + Integer.toString(intent.getIntExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RATING, -1)) + "/10" + "</b>";
                movieRating.setText(Html.fromHtml(rating));
            }
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RELEASE_DATE)) {
                String releaseDate = "Release Date: " + "<b> " + intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RELEASE_DATE) + "</b>";
                movieReleaseDate.setText(Html.fromHtml(releaseDate));
            }
        }

    }
}
