package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Silky on 01/02/2018.
 */

public class FavMovieContract {

    public static String CONTENT_AUTHORITY = "com.example.android.popularmovies.data";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

            public static final class FavMovieEntry implements BaseColumns{
                public static final String TABLE_NAME = "Movie Favourites";
                public static final String TITLE = "Movie Title";
                public static final String MOVIE_IMAGE_PATH = "Movie Image Path";

                // create content uri
                public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                        .appendPath(TABLE_NAME).build();

                //base type directory for multiple entries
                public static final String CONTENT_DIR_TYPE =
                        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

                public static final String CONTENT_ITEM_TYPE =
                        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
            }
}
