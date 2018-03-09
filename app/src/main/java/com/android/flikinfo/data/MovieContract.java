package com.android.flikinfo.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by harminder on 07/03/18.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.android.flikinfo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_RATING = "rating";

    }
}
