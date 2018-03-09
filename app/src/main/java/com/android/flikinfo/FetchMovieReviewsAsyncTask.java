package com.android.flikinfo;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by harminder on 06/03/18.
 */

public class FetchMovieReviewsAsyncTask extends AsyncTask<String, Void, MovieReviewsData[]>{

    private ReviewsAsyncTaskListener listener;

    public interface ReviewsAsyncTaskListener {
        void doReviewsDataFetched(MovieReviewsData[] movieReviewsData);

        void showReviewsErrorMessage();

        void showReviewsLoadingIndicator(boolean show);
    }

    public void setListener(ReviewsAsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.showReviewsLoadingIndicator(true);
    }

    @Override
    protected MovieReviewsData[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String url = params[0];

        URL reviewsUrl = NetworkUtils.buildUrl(url);
        MovieReviewsData[] movieReviewsData;

        try {
            String JSONReviewResponse = NetworkUtils.getResponseFromHttpUrl(reviewsUrl);

            JSONObject jsonObject = new JSONObject(JSONReviewResponse);
            JSONArray resultsArray = jsonObject.getJSONArray(FlikInfoConstants.RESULT_KEY);
            movieReviewsData = new MovieReviewsData[resultsArray.length()];
            for (int i = 0; i < movieReviewsData.length; i++) {
                JSONObject reviewObject = resultsArray.getJSONObject(i);
                MovieReviewsData currentReview = new MovieReviewsData();
                currentReview.review = reviewObject.getString(FlikInfoConstants.REVIEW_KEY);
                currentReview.author = reviewObject.getString(FlikInfoConstants.AUTHOR_KEY);
                movieReviewsData[i] = currentReview;
            }
            return movieReviewsData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(MovieReviewsData[] movieReviewsData) {
        listener.showReviewsLoadingIndicator(false);
        if (movieReviewsData != null && listener != null) {
            listener.doReviewsDataFetched(movieReviewsData);
        } else {
            listener.showReviewsErrorMessage();
        }
    }
}
