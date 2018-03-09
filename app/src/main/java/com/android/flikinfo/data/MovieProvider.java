package com.android.flikinfo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.flikinfo.data.MovieContract.MovieEntry;

/**
 * Created by harminder on 07/03/18.
 */

public class MovieProvider extends ContentProvider {

    public static final int CODE_FAVORITES = 100;
    public static final int CODE_FAVORITES_WITH_ID = 101;

    private MovieDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_FAVORITES, CODE_FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#", CODE_FAVORITES_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_FAVORITES_WITH_ID: {
                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{movieId};
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_FAVORITES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CODE_FAVORITES_WITH_ID:
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case CODE_FAVORITES_WITH_ID:
                // Get the task ID from the URI path
                String movieId = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(MovieEntry.TABLE_NAME, "movie_id=?", new String[]{movieId});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
