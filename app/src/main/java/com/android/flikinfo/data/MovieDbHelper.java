package com.android.flikinfo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.flikinfo.data.MovieContract.MovieEntry;

/**
 * Created by harminder on 07/03/18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE =
                        "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL, "              +
                        MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"                     +
                        MovieEntry.COLUMN_MOVIE_OVERVIEW   + " TEXT NOT NULL, "               +
                                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "            +
                        MovieEntry.COLUMN_MOVIE_RELEASE_DATE   + " TEXT NOT NULL, "           +
                        MovieEntry.COLUMN_MOVIE_RATING   + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
