package com.example.android.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.android.popularmovies.data.FavMovieContract;

public class FavMoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private FavMoviesAdapter mFavMoviesAdapter;
    private RecyclerView mFavMoviesRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;


    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;

    public static final String[] FAV_MOVIES_PROJECTION = {
            FavMovieContract.FavMovieEntry.MOVIE_ID,
            FavMovieContract.FavMovieEntry.ORIGINAL_TITLE,
            FavMovieContract.FavMovieEntry.RELEASE_DATE,
            FavMovieContract.FavMovieEntry.MOVIE_IMAGE_THUMB_PATH,
            FavMovieContract.FavMovieEntry.MOVIE_IMAGE_PATH,
            FavMovieContract.FavMovieEntry.PLOT_SYNOPSIS,
            FavMovieContract.FavMovieEntry.USER_RATING,
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_ORIGINAL_TITLE = 1;
    public static final int INDEX_RELEASE_DATE = 2;
    public static final int INDEX_MOVIE_IMAGE_THUMB_PATH = 3;
    public static final int INDEX_MOVIE_IMAGE_PATH = 4;
    public static final int INDEX_MOVIE_PLOT_SYNOPSIS = 5;
    public static final int INDEX_USER_RATING = 6;

    private static final int ID_FAV_MOVIE_LOADER_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_movies);

        mFavMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_fav_movies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator_fav);
        mErrorMessage = (TextView) findViewById(R.id.error_fav_msg);

        int numberOfColumns = 4;

        mFavMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mFavMoviesRecyclerView.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(ID_FAV_MOVIE_LOADER_ID, null, this);

        mFavMoviesAdapter = new FavMoviesAdapter(this, new FavMoviesAdapter.OnFavMovieItemClickListener() {
            @Override
            public void onItemClick(MovieInfo item) {

                Class destinationClass = MovieDetailActivity.class;
                Intent intentStartActivity = new Intent(getApplicationContext(), destinationClass);

                intentStartActivity.putExtra("mMovieDetails", item);
                startActivity(intentStartActivity);

            }
        });

        mFavMoviesRecyclerView.setAdapter(mFavMoviesAdapter);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch(loaderId){

            case ID_FAV_MOVIE_LOADER_ID:
                Uri favMovieQueryUri = FavMovieContract.FavMovieEntry.CONTENT_URI;

                CursorLoader cursorLoader = new CursorLoader(this,
                        favMovieQueryUri,
                        FAV_MOVIES_PROJECTION,
                        null,
                        null,
                        null
                );
                return cursorLoader;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mFavMoviesAdapter.swapCursor(data);

        if(mPosition == RecyclerView.NO_POSITION)mPosition = 0;

        mFavMoviesRecyclerView.smoothScrollToPosition(mPosition);

        if(data.getCount() != 0){
            showFavMoviesView();
        } else {
            mErrorMessage.setText("You have not favourited any movies.");
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void showFavMoviesView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mFavMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavMoviesAdapter.swapCursor(null);
    }
}
