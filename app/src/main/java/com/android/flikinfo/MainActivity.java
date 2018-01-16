package com.android.flikinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieThumbnailClickHandler, FetchMoviesAsyncTask.MovieAsyncTaskListener {

    private RecyclerView mRecyclerView;
    private MoviesAdapter moviesAdapter;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private List<MovieData> movieData;
    private boolean sortByPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieData = new ArrayList<>();
        mErrorMessage = findViewById(R.id.tv_error_message);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.movies_recyclerview);
        moviesAdapter = new MoviesAdapter(this, this);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, FlikInfoConstants.GRID_NUM_COLUMNS));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(moviesAdapter);

        loadMoviesData(FlikInfoConstants.URLConstants.POPULAR_MOVIES_URL);
    }

    @Override
    public void onClick(MovieData movieData) {
        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_TITLE, movieData.movieName);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_OVERVIEW, movieData.overview);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RATING, movieData.rating);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RELEASE_DATE, movieData.releaseDate);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_POSTER_PATH, movieData.imagePath);
        startActivity(intent);
    }

    @Override
    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showMovieThumbnail() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingIndicator(boolean show) {
        if (show) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    private void loadMoviesData(String url) {
        showMovieThumbnail();
        FetchMoviesAsyncTask fetchMoviesAsyncTask = new FetchMoviesAsyncTask();
        fetchMoviesAsyncTask.setListener(this);
        fetchMoviesAsyncTask.execute(url);
    }

    @Override
    public void doMoviesDataFetched(MovieData[] movies) {
        showMovieThumbnail();
        if (movies != null) {
            movieData.addAll(Arrays.asList(movies));
        }
        if (movies == null) {
            movieData.clear();
        }
        moviesAdapter.setMovieData(movieData);
        moviesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_by, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.movies_sort_by) {
            if (item.getTitle().equals("Sort By: Rating")) {
                sortByPopular = false;
                item.setTitle("Sort By: Popularity");
                doMoviesDataFetched(null);
                loadMoviesData(FlikInfoConstants.URLConstants.TOP_RATED_MOVIES_URL);
            } else {
                sortByPopular = true;
                item.setTitle("Sort By: Rating");
                doMoviesDataFetched(null);
                loadMoviesData(FlikInfoConstants.URLConstants.POPULAR_MOVIES_URL);
            }
        }

        if(itemId == R.id.ref_movie) {
            if (sortByPopular) {
                doMoviesDataFetched(null);
                loadMoviesData(FlikInfoConstants.URLConstants.POPULAR_MOVIES_URL);
            } else {
                doMoviesDataFetched(null);
                loadMoviesData(FlikInfoConstants.URLConstants.TOP_RATED_MOVIES_URL);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
