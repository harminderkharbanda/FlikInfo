package com.android.flikinfo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.flikinfo.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements TrailersAdapter.TrailerClickHandler,
        FetchMovieTrailersAsyncTask.TrailerAsyncTaskListener, ReviewsAdapter.ReviewsClickHandler,
        FetchMovieReviewsAsyncTask.ReviewsAsyncTaskListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MovieDetailActivity";
    private RecyclerView mRecyclerView;
    private TrailersAdapter trailersAdapter;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private List<MovieTrailersData> movieTrailersData;

    private RecyclerView mReviewsRecyclerView;
    private TextView mReviewsErrorMessage;
    private ProgressBar mReviewsLoadingIndicator;
    private ReviewsAdapter reviewsAdapter;
    private List<MovieReviewsData> movieReviewsData;

    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieOverview;
    private TextView movieRating;
    private TextView movieReleaseDate;

    private int movieId;
    private ImageView favoriteImage;

    private static final int TASK_LOADER_ID = 0;
    private Cursor movieCursor;
    private String posterPath;

    private ScrollView mScrollView;

    private static final String TRAILERS_RV_SAVED_POSITION = "trailers_rv_saved_position";
    private static final String REVIEWS_RV_SAVED_POSITION = "reviews_rv_saved_position";
    private Parcelable tailersSavedState;
    private Parcelable reviewsSavedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mScrollView = findViewById(R.id.movie_detail_scrollview);

        movieTitle = findViewById(R.id.tv_movie_title);
        moviePoster = findViewById(R.id.iv_movie_image);
        movieOverview = findViewById(R.id.tv_movie_overview);
        movieRating = findViewById(R.id.tv_movie_rating);
        movieReleaseDate = findViewById(R.id.tv_movie_release_date);

        movieTrailersData = new ArrayList<>();
        mErrorMessage = findViewById(R.id.trailers_error_message);
        mLoadingIndicator = findViewById(R.id.pb_trailers_loading_indicator);
        mRecyclerView = findViewById(R.id.trailers_recyclerview);
        trailersAdapter = new TrailersAdapter(this, this);

        movieReviewsData = new ArrayList<>();
        mReviewsErrorMessage = findViewById(R.id.reviews_error_message);
        mReviewsLoadingIndicator = findViewById(R.id.pb_reviews_loading_indicator);
        mReviewsRecyclerView = findViewById(R.id.reviews_recyclerview);
        reviewsAdapter = new ReviewsAdapter(this);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(trailersAdapter);

        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setAdapter(reviewsAdapter);

        favoriteImage = findViewById(R.id.iv_favorite);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.detail_activity_title);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_TITLE)) {
                movieTitle.setText(intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_TITLE).toUpperCase());
            }
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_POSTER_PATH)) {
                posterPath = intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_POSTER_PATH);
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
            if(intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_ID)) {
             movieId = intent.getIntExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_ID, -1);
             loadTrailersData(FlikInfoConstants.URLConstants.MOVIE_BASE_URL + movieId + FlikInfoConstants.URLConstants.VIDEOS);
             loadReviewsData(FlikInfoConstants.URLConstants.MOVIE_BASE_URL + movieId + FlikInfoConstants.URLConstants.REVIEWS);
            }
        }
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(TASK_LOADER_ID);
    }

    private void loadTrailersData(String url) {
        showTrailersThumbnail();
        FetchMovieTrailersAsyncTask fetchMovieTrailersAsyncTask = new FetchMovieTrailersAsyncTask();
        fetchMovieTrailersAsyncTask.setListener(this);
        fetchMovieTrailersAsyncTask.execute(url);
    }

    private void loadReviewsData(String url) {
        showReviewsThumbnail();
        FetchMovieReviewsAsyncTask fetchMovieReviewsAsyncTask = new FetchMovieReviewsAsyncTask();
        fetchMovieReviewsAsyncTask.setListener(this);
        fetchMovieReviewsAsyncTask.execute(url);
    }

    private void showTrailersThumbnail() {
        mErrorMessage.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showReviewsThumbnail() {
        mReviewsErrorMessage.setVisibility(View.GONE);
        mReviewsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void doTrailersDataFetched(MovieTrailersData[] trailers) {
        showTrailersThumbnail();
        if (trailers != null) {
            movieTrailersData.addAll(Arrays.asList(trailers));
        }
        if (trailers == null) {
            movieTrailersData.clear();
        }
        trailersAdapter.setTrailerData(movieTrailersData);
        restoreTrailersPosition();
        trailersAdapter.notifyDataSetChanged();
    }

    @Override
    public void doReviewsDataFetched(MovieReviewsData[] reviews) {
        showReviewsThumbnail();
        if (reviews != null) {
            movieReviewsData.addAll(Arrays.asList(reviews));
        }
        if (reviews == null) {
            movieReviewsData.clear();
        }
        reviewsAdapter.setReviewsData(movieReviewsData);
        restoreReviewsPosition();
        reviewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showReviewsErrorMessage() {
        mReviewsRecyclerView.setVisibility(View.INVISIBLE);
        mReviewsErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showReviewsLoadingIndicator(boolean show) {
        if (show) {
            mReviewsLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mReviewsLoadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingIndicator(boolean show) {
        if (show) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mLoadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(MovieTrailersData movieTrailersData) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FlikInfoConstants.URLConstants.YOUTUBE_BASE_URL + movieTrailersData.key));
        startActivity(intent);
    }

    @Override
    public void onClick(MovieReviewsData movieReviewsData) {
        String review = movieReviewsData.review;
        String author = movieReviewsData.author;
        Intent intent = new Intent(this, FullReviewActivity.class);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_REVIEW, review);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_REVIEW_AUTHOR, author);
        startActivity(intent);
    }

    public void setFavorite(View view) {
        if (movieCursor.getCount() == 0) {

            String title = movieTitle.getText().toString();
            String overView = movieOverview.getText().toString();
            String releaseDate = movieReleaseDate.getText().toString();
            String rating = movieRating.getText().toString();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(MovieEntry.COLUMN_MOVIE_TITLE, title);
            contentValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, overView);
            contentValues.put(MovieEntry.COLUMN_POSTER_PATH, posterPath);
            contentValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            contentValues.put(MovieEntry.COLUMN_MOVIE_RATING, rating);

            Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build(), contentValues);

            if (uri != null) {
                favoriteImage.setImageResource(R.drawable.favorite);
                Toast.makeText(getBaseContext(), "Movie added to your favorites", Toast.LENGTH_SHORT).show();
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this).forceLoad();
            }
        } else {
            int n = getContentResolver().delete(MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build(), null, null);
            Toast.makeText(getBaseContext(), "Movie removed from favorites", Toast.LENGTH_SHORT).show();
            favoriteImage.setImageResource(R.drawable.star);
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this).forceLoad();
        }

    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                if (mCursor != null) {
                    deliverResult(mCursor);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(MovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build(),
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        movieCursor = data;
        if (movieCursor.getCount() == 0) {
            favoriteImage.setImageResource(R.drawable.star);
        }else {
            favoriteImage.setImageResource(R.drawable.favorite);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        movieCursor = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    private void restoreTrailersPosition() {
        if (tailersSavedState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(tailersSavedState);
        }
    }

    private void restoreReviewsPosition() {
        if (reviewsSavedState != null) {
            mReviewsRecyclerView.getLayoutManager().onRestoreInstanceState(reviewsSavedState);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
        outState.putParcelable(TRAILERS_RV_SAVED_POSITION, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelable(REVIEWS_RV_SAVED_POSITION, mReviewsRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("SCROLL_POSITION");
        if(position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
        tailersSavedState = savedInstanceState.getParcelable(TRAILERS_RV_SAVED_POSITION);
        reviewsSavedState = savedInstanceState.getParcelable(REVIEWS_RV_SAVED_POSITION);
    }
}
