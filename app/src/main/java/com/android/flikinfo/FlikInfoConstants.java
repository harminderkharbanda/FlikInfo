package com.android.flikinfo;

/**
 * Created by harminder on 01/01/18
 */

class FlikInfoConstants {

    public final static int GRID_NUM_COLUMNS = 2;
    public final static String RESULT_KEY = "results";
    public final static String MOVIE_TITLE = "title";
    public final static String MOVIE_OVERVIEW = "overview";
    public final static String MOVIE_RELEASE_DATE = "release_date";
    public final static String MOVIE_POSTER_PATH = "poster_path";
    public final static String MOVIE_RATING = "vote_average";
    public final static String MOVIE_ID = "id";
    public final static String TRAILER_KEY = "key";
    public final static String REVIEW_KEY = "content";
    public final static String AUTHOR_KEY = "author";


    public class URLConstants {
        public final static String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
        public final static String POPULAR_MOVIES_URL = MOVIE_BASE_URL + "popular";
        public final static String TOP_RATED_MOVIES_URL = MOVIE_BASE_URL + "top_rated";
        public final static String API_KEY = "";
        public final static String PARAM_API_KEY = "api_key";
        public final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
        public final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
        public final static String YOUTUBE_THUMBNAIL_BASE_URL = "http://img.youtube.com/vi/";
        public final static String VIDEOS = "/videos";
        public final static String REVIEWS = "/reviews";
    }

    public class IntentConstants {
        public final static String INTENT_KEY_MOVIE_TITLE = "title";
        public final static String INTENT_KEY_POSTER_PATH = "poster_path";
        public final static String INTENT_KEY_MOVIE_OVERVIEW = "overview";
        public final static String INTENT_KEY_MOVIE_RELEASE_DATE = "release_date";
        public final static String INTENT_KEY_MOVIE_RATING = "vote_average";
        public final static String INTENT_KEY_MOVIE_ID = "id";
        public final static String INTENT_KEY_REVIEW = "review";
        public final static String INTENT_KEY_REVIEW_AUTHOR = "author";
    }
}
