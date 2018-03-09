package com.android.flikinfo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.flikinfo.data.MovieContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieThumbnailClickHandler,
        FetchMoviesAsyncTask.MovieAsyncTaskListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private MoviesAdapter moviesAdapter;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private ImageView nothingImage;
    private List<MovieData> movieData;
    private boolean sortByPopular = true;
    MovieData[] movieArrayData;
    private static final int TASK_LOADER_ID = 1;
    int movieType = 1;


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

        nothingImage = findViewById(R.id.iv_nothing);

        loadMoviesData(FlikInfoConstants.URLConstants.POPULAR_MOVIES_URL);
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public void onClick(MovieData movieData) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_TITLE, movieData.movieName);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_OVERVIEW, movieData.overview);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RATING, movieData.rating);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_RELEASE_DATE, movieData.releaseDate);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_POSTER_PATH, movieData.imagePath);
        intent.putExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_MOVIE_ID, movieData.id);
        startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(TASK_LOADER_ID);

    }

    @Override
    public void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showMovieThumbnail() {
        nothingImage.setVisibility(View.INVISIBLE);
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

        if (itemId == R.id.movie_favorites) {
            movieType = 3;
            doMoviesDataFetched(null);
            doMoviesDataFetched(movieArrayData);
            if (movieArrayData.length == 0) {
                nothingImage.setVisibility(View.VISIBLE);
                Toast.makeText(this, "You have not marked any movie as favorite", Toast.LENGTH_SHORT).show();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();
            movieArrayData = new MovieData[cursor.getCount()];
            int i = 0;
            while (!cursor.isAfterLast()) {
                MovieData currentMovie = new MovieData();
                currentMovie.id = Integer.valueOf(cursor.getString(cursor.getColumnIndex("movie_id")));
                currentMovie.movieName = cursor.getString(cursor.getColumnIndex("title"));
                currentMovie.overview = cursor.getString(cursor.getColumnIndex("overview"));
                currentMovie.imagePath = cursor.getString(cursor.getColumnIndex("poster_path"));
                Log.d(TAG, cursor.getString(cursor.getColumnIndex("poster_path")));
                currentMovie.releaseDate = cursor.getString(cursor.getColumnIndex("release_date"));
                currentMovie.rating = Integer.valueOf(cursor.getString(cursor.getColumnIndex("rating")).charAt(8));
                movieArrayData[i] = currentMovie;
                cursor.moveToNext();
                i++;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this).forceLoad();
        if (movieType == 3) {
            movieType = 1;
            doMoviesDataFetched(null);
            loadMoviesData(FlikInfoConstants.URLConstants.POPULAR_MOVIES_URL);
        }
    }

}
