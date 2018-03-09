package com.android.flikinfo;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by harminder on 07/01/18
 */

class FetchMoviesAsyncTask extends AsyncTask<String, Void, MovieData[]> {

    private MovieAsyncTaskListener listener;

    public interface MovieAsyncTaskListener {
        void doMoviesDataFetched(MovieData[] movies);
        void showErrorMessage();
        void showLoadingIndicator(boolean show);
    }

    public void setListener (MovieAsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.showLoadingIndicator(true);
    }

    @Override
    protected MovieData[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String url = params[0];

        URL moviesUrl = NetworkUtils.buildUrl(url);
        MovieData[] movieData;

        try {
            String JSONMovieResponse = NetworkUtils.getResponseFromHttpUrl(moviesUrl);

            JSONObject jsonObject = new JSONObject(JSONMovieResponse);
            JSONArray resultsArray = jsonObject.getJSONArray(FlikInfoConstants.RESULT_KEY);
            movieData = new MovieData[resultsArray.length()];
            for (int i = 0; i < movieData.length; i++) {
                JSONObject movieObject = resultsArray.getJSONObject(i);
                MovieData currentMovie = new MovieData();
                currentMovie.movieName = movieObject.getString(FlikInfoConstants.MOVIE_TITLE);
                currentMovie.imagePath = movieObject.getString(FlikInfoConstants.MOVIE_POSTER_PATH);
                currentMovie.overview = movieObject.getString(FlikInfoConstants.MOVIE_OVERVIEW);
                currentMovie.releaseDate = movieObject.getString(FlikInfoConstants.MOVIE_RELEASE_DATE);
                currentMovie.rating = movieObject.getInt(FlikInfoConstants.MOVIE_RATING);
                currentMovie.id = movieObject.getInt(FlikInfoConstants.MOVIE_ID);
                movieData[i] = currentMovie;
            }
            return movieData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(MovieData[] movieData) {
        listener.showLoadingIndicator(false);
        if (movieData != null && listener!= null) {
            listener.doMoviesDataFetched(movieData);
        } else {
            listener.showErrorMessage();
        }
    }
}
