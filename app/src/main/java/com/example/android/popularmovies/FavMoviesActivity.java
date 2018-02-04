package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies.data.FavMovieContract;

/**This class will use a cursor loader to query off the favourite movies database and upload the
 * views into recycler view.
 * Created by Silky on 04/02/2018.
 */

public class FavMoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
    FavMoviesAdapter.OnFavMovieItemClickListener{

    private FavMoviesAdapter mFavMoviesAdapter;
    private RecyclerView mFavMoviesRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private MovieInfo[] favMovieInfos;

    private Cursor mCursor;

    private ProgressBar mLoadingIndicator;

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
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_fav_movies);

        mFavMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_favMovies);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_favMovie_loading_indicator);

        int numberOfColumns = 4;

        mFavMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mFavMoviesRecyclerView.setHasFixedSize(true);



        mFavMoviesRecyclerView.setAdapter(mFavMoviesAdapter);

        getSupportLoaderManager().initLoader(ID_FAV_MOVIE_LOADER_ID, null, (android.support.v4.app.LoaderManager.LoaderCallbacks<Object>) this);

        mFavMoviesAdapter = new FavMoviesAdapter(this, favMovieInfos, new FavMoviesAdapter.OnFavMovieItemClickListener() {
            @Override
            public void onItemClick(MovieInfo item) {

                Class destinationClass = MovieDetailActivity.class;
                Intent intentStartActivity = new Intent(getApplicationContext(), destinationClass);

                intentStartActivity.putExtra("mMovieDetails", item);
                startActivity(intentStartActivity);

            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch(loaderId){

            case ID_FAV_MOVIE_LOADER_ID:
                Uri favMovieQueryUri = FavMovieContract.FavMovieEntry.CONTENT_URI;

                return new CursorLoader(this,
                        favMovieQueryUri,
                        FAV_MOVIES_PROJECTION,
                        null,
                        null,
                        null
                        );
                default:
                    throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mFavMoviesAdapter.swapCursor(data);
        mCursor = data; //we are going to use the cursor to put into into a MovieInfo object
        if(mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mFavMoviesRecyclerView.smoothScrollToPosition(mPosition);
        if(data.getCount() != 0) showFavMoviesView();
        putCursorDataIntoMovieInfo();
    }

    private void putCursorDataIntoMovieInfo() {

        int noOfRows = mCursor.getCount();
        favMovieInfos = new MovieInfo[noOfRows];

        for(int i=1; i <= noOfRows ; i++){
            mCursor.moveToPosition(i);
            int movieID = mCursor.getInt(INDEX_MOVIE_ID);
            String originalTitle = mCursor.getString(INDEX_ORIGINAL_TITLE);
            String releaseDate = mCursor.getString(INDEX_RELEASE_DATE);
            String imageThumbPath = mCursor.getString(INDEX_MOVIE_IMAGE_THUMB_PATH);
            String imagePath = mCursor.getString(INDEX_MOVIE_IMAGE_PATH);
            String plot = mCursor.getString(INDEX_MOVIE_PLOT_SYNOPSIS);
            Double userRating = mCursor.getDouble(INDEX_USER_RATING);

            MovieInfo movieInfo = new MovieInfo(movieID, originalTitle, releaseDate, imageThumbPath, imagePath, plot, userRating);
            favMovieInfos[i-1] = movieInfo;
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


    @Override
    public void onItemClick(MovieInfo movieInfo) {

    }
}
