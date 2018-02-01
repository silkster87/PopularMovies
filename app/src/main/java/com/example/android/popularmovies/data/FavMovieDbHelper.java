package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
