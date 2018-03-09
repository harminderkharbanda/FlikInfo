package com.android.flikinfo;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by harminder on 05/03/18.
 */

public class FetchMovieTrailersAsyncTask extends AsyncTask<String, Void, MovieTrailersData[]> {

    private TrailerAsyncTaskListener listener;

    public interface TrailerAsyncTaskListener {
        void doTrailersDataFetched(MovieTrailersData[] movieTrailersData);

        void showErrorMessage();

        void showLoadingIndicator(boolean show);
    }

    public void setListener(TrailerAsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.showLoadingIndicator(true);
    }

    @Override
    protected MovieTrailersData[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String url = params[0];

        URL trailersUrl = NetworkUtils.buildUrl(url);
        MovieTrailersData[] movieTrailersData;

        try {
            String JSONMovieResponse = NetworkUtils.getResponseFromHttpUrl(trailersUrl);

            JSONObject jsonObject = new JSONObject(JSONMovieResponse);
            JSONArray resultsArray = jsonObject.getJSONArray(FlikInfoConstants.RESULT_KEY);
            movieTrailersData = new MovieTrailersData[resultsArray.length()];
            for (int i = 0; i < movieTrailersData.length; i++) {
                JSONObject trailerObject = resultsArray.getJSONObject(i);
                MovieTrailersData currentTrailer = new MovieTrailersData();
                currentTrailer.key = trailerObject.getString(FlikInfoConstants.TRAILER_KEY);
                movieTrailersData[i] = currentTrailer;
            }
            return movieTrailersData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    protected void onPostExecute(MovieTrailersData[] movieTrailersData) {
        listener.showLoadingIndicator(false);
        if (movieTrailersData != null && listener != null) {
            listener.doTrailersDataFetched(movieTrailersData);
        } else {
            listener.showErrorMessage();
        }
    }

}

