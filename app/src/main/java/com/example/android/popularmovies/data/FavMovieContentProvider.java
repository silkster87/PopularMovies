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

        sUriMatcher.addURI(FavMovieContract.CONTENT_AUTHORITY, FavMovieContract.PATH_MOVIES, DIRECTORY_FAVMOVIES);
        sUriMatcher.addURI(FavMovieContract.CONTENT_AUTHORITY, FavMovieContract.PATH_MOVIES + "/#", DIRECTORY_FAVMOVIES_ITEM);

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
                retCursor = db.query(FavMovieContract.FavMovieEntry.TABLE_NAME, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                return retCursor;

                default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match){
            case DIRECTORY_FAVMOVIES:{
                return FavMovieContract.FavMovieEntry.CONTENT_DIR_TYPE;
            }
            case DIRECTORY_FAVMOVIES_ITEM:{
                return FavMovieContract.FavMovieEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable


    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mFavMoveDbHelper.getWritableDatabase();
        Uri returnUri;
        //inserting a single row of data when user favourited a movie
        switch (sUriMatcher.match(uri)){
            case DIRECTORY_FAVMOVIES: {
                long _id = db.insert(FavMovieContract.FavMovieEntry.TABLE_NAME, null, contentValues);

                if(_id > 0){
                    returnUri = ContentUris.withAppendedId(FavMovieContract.BASE_CONTENT_URI, _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
       final SQLiteDatabase db = mFavMoveDbHelper.getWritableDatabase();
        //we only want to delete one item when user unchecks favourite movie
       int match = sUriMatcher.match(uri);
       int itemsDeleted;

       switch(match){
           case(DIRECTORY_FAVMOVIES):{
             itemsDeleted =  db.delete(FavMovieContract.FavMovieEntry.TABLE_NAME, s, strings);
           break;
           }
           default:
               throw new UnsupportedOperationException("Unknown Uri " + uri);
       }

       if(itemsDeleted !=0){
           getContext().getContentResolver().notifyChange(uri, null);
       }
        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int favMovieItemsUpdated;

        int match = sUriMatcher.match(uri);

        switch(match){
            case(DIRECTORY_FAVMOVIES_ITEM):
                String id = uri.getPathSegments().get(1);
                favMovieItemsUpdated = mFavMoveDbHelper.getWritableDatabase().update(FavMovieContract.FavMovieEntry.TABLE_NAME, contentValues,
                        "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            if(favMovieItemsUpdated !=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return favMovieItemsUpdated;
    }
}
