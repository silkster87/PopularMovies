package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;



import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenMovieJsonUtils;


import org.json.JSONArray;

import java.net.URL;
/* This is a Popular Movies app that will query data off theMovieDB using an API key. The results will
be displayed in a Recycler View in a Grid layout in pictures. The user can click onto a picture of a
movie to get more details. The grid view can be sorted out by most popular or highest rated.
*
* */


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieInfo[]>{

    private RecyclerView mRecyclerView;
    private TextView mErrorMessage;
    private static final int MOVIEDB_QUERY_LOADER = 22;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mLoadingIndicator;
    private String movieEndpoint = null;
    private MovieInfo[] movieInfos;
    private static final String PREFS_NAME = "Settings_Prefs";
    private SharedPreferences settings;
    private static final String PREFS_KEY = "Prefs_String";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Sharedpreferences to get preference from specified file
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movies);

        mErrorMessage = (TextView) findViewById(R.id.error_msg);

        int numberOfColumns = 4;

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(movieInfos, new MoviesAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(MovieInfo items) {
                //When user clicks on a picture we want to start a new activity to display more info.


            Class destinationClass = MovieDetailActivity.class;
            Intent intentStartActivity = new Intent(getApplicationContext(), destinationClass);

            //Need to implement Parcelable and then put the parcel in putExtra
            intentStartActivity.putExtra("mMovieDetails", items);
            startActivity(intentStartActivity);

            }
        });

        mRecyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LoaderManager.LoaderCallbacks<MovieInfo[]> callback = MainActivity.this;

        getSupportLoaderManager().initLoader(MOVIEDB_QUERY_LOADER, null, callback);

    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MovieInfo[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieInfo[]>(this) {

            //MovieInfo[] movieInfoArray = null;

            @Override
            protected void onStartLoading() {
                if (movieInfos != null) {
                    deliverResult(movieInfos);
                } else {
                    forceLoad();
                }
            }

            @Override
            public MovieInfo[] loadInBackground() {

                movieEndpoint = getValue(MainActivity.this); //Get value in sharedPreferences

                if(movieEndpoint == null){
                    movieEndpoint = "/movie/popular?"; //By default we are uploading most popular movies
                }

                URL movieDBURL =  NetworkUtils.buildUrl(movieEndpoint);

                MovieInfo[] movieInfoArray;
                try {
                        String jsonMovieDataResponse =
                                NetworkUtils.getResponseFromHttpUrl(movieDBURL);
                        JSONArray simpleJsonMovieData =
                                OpenMovieJsonUtils.getJsonArrayOfMovieResults(MainActivity.this, jsonMovieDataResponse);

                    if (simpleJsonMovieData == null) throw new AssertionError();
                    movieInfoArray = new MovieInfo[simpleJsonMovieData.length()];

                        for(int i = 0; i < simpleJsonMovieData.length();i++){

                            int vMovieID = simpleJsonMovieData.getJSONObject(i).getInt("id");
                            String vOriginalTitle = simpleJsonMovieData.getJSONObject(i).getString("original_title");
                            String vReleaseDate = simpleJsonMovieData.getJSONObject(i).getString("release_date");
                            String vImageThumbPath = simpleJsonMovieData.getJSONObject(i).getString("backdrop_path");
                            String vImagePath = simpleJsonMovieData.getJSONObject(i).getString("poster_path");
                            String vPlotSynopsis = simpleJsonMovieData.getJSONObject(i).getString("overview");
                            Double vUserRating = simpleJsonMovieData.getJSONObject(i).getDouble("vote_average");

                            MovieInfo vMovieInfo = new MovieInfo(vMovieID, vOriginalTitle, vReleaseDate, vImageThumbPath, vImagePath, vPlotSynopsis, vUserRating);

                            movieInfoArray[i] = vMovieInfo;
                        }
                        return movieInfoArray;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }


            @Override
            public void deliverResult(MovieInfo[] data) {
                 movieInfos = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieInfo[]> loader, MovieInfo[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMoviesAdapter.setMovieData(data); //The method setMovieData will load up images.
 
        if(null == data){
            showErrorMessage();
        }else{
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieInfo[]> loader) {

    }

    private void showMovieDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }




    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemSelected = item.getItemId();
        if (menuItemSelected == R.id.high_rated){
            movieEndpoint = "/movie/top_rated?"; //user has selected sort by highest rated
            save(getApplicationContext(), "/movie/top_rated?" );
            restartLoader();
        } else if (menuItemSelected == R.id.popular_movies){
            movieEndpoint = "/movie/popular?"; //user has selected sort by most popular
            save(getApplicationContext(), "/movie/popular?" );
            restartLoader();
        } else if (menuItemSelected == R.id.fav_movies){
            //Start new activity to FavMoviesActivity
            Class destinationClass = FavMoviesActivity.class;
            Context context = this;
            Intent intentToStartFavMovies = new Intent(context, destinationClass);
            startActivity(intentToStartFavMovies);
        }
        //Need to get AsyncTask to run again to fetch new query
        return super.onOptionsItemSelected(item);
    }

    private void restartLoader(){
        //Need to get AsyncTask to run again to fetch new query
        LoaderManager.LoaderCallbacks<MovieInfo[]> callback = MainActivity.this;
        getSupportLoaderManager().restartLoader(MOVIEDB_QUERY_LOADER, null, callback);
    }

    private void save(Context context, String text){ //save method for shardPreferences when user chooses settings
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_KEY, text);
        editor.apply();
    }

    private String getValue(Context context){ //getValue method for sharedPreferences to retrieve settings
        String text;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(PREFS_KEY, null);
        return  text;
    }


}
