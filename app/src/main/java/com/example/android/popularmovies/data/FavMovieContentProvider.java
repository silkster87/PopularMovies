package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Silky on 01/02/2018.
 */

public class FavMovieContentProvider extends ContentProvider {

    private FavMovieDbHelper mFavMoveDbHelper;

    public static final int DIRECTORY_FAVMOVIES = 100;

    public static final int DIRECTORY_FAVMOVIES_ITEM = 101;

    public static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(FavMovieContract.CONTENT_AUTHORITY, FavMovieContract.FavMovieEntry.TABLE_NAME, DIRECTORY_FAVMOVIES);
        sUriMatcher.addURI(FavMovieContract.CONTENT_AUTHORITY, FavMovieContract.FavMovieEntry.TABLE_NAME + "/#", DIRECTORY_FAVMOVIES_ITEM);

        return sUriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavMoveDbHelper = new FavMovieDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mFavMoveDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match){
            case(DIRECTORY_FAVMOVIES):
                retCursor = db.query(FavMovieContract.FavMovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                return retCursor;

            case(DIRECTORY_FAVMOVIES_ITEM):
                retCursor = db.query(FavMovieContract.FavMovieEntry.TABLE_NAME, projection,
                        FavMovieContract.FavMovieEntry._ID + " = ?", new String[]{String.valueOf(ContentUris.parseId(uri))},
                null, null, sortOrder);
                return retCursor;

                default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
