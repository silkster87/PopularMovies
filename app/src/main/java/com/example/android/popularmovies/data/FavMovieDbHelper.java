package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Silky on 01/02/2018.
 */

public class FavMovieDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = FavMovieDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "favMovies.db";

    private static final int DATABASE_VERSION = 1;

    public FavMovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVMOVIE_TABLE = "CREATE TABLE " +
                FavMovieContract.FavMovieEntry.TABLE_NAME + "( " +
                FavMovieContract.FavMovieEntry._ID +"INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavMovieContract.FavMovieEntry.TITLE + "TEXT NOT NULL, " +
                FavMovieContract.FavMovieEntry.MOVIE_IMAGE_PATH + "TEXT NOT NULL, " + " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVMOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
         + ". Old data will be destroyed");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavMovieContract.FavMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}