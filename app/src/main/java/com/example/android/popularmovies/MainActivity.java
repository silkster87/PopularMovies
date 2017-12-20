package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
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



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieInfo[]>{

    private RecyclerView mRecyclerView;
    private TextView mErrorMessage;
    private static final int MOVIEDB_QUERY_LOADER = 22;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mLoadingIndicator;
    public String movieEndpoint = null;
    private MovieInfo[] movieInfos;
    public static final String PREFS_NAME = "Settings_Prefs";
    public SharedPreferences settings;
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

        int loaderID = MOVIEDB_QUERY_LOADER;

        LoaderManager.LoaderCallbacks<MovieInfo[]> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderID, bundleForLoader, callback);

    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MovieInfo[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieInfo[]>(this) {

            MovieInfo[] movieInfoArray = null;

            @Override
            protected void onStartLoading() {
                if (movieInfoArray != null) {
                    deliverResult(movieInfoArray);
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

                        movieInfoArray = new MovieInfo[simpleJsonMovieData.length()];

                        for(int i = 0; i < simpleJsonMovieData.length();i++){

                            String vOriginalTitle = simpleJsonMovieData.getJSONObject(i).getString("original_title");
                            String vReleaseDate = simpleJsonMovieData.getJSONObject(i).getString("release_date");
                            String vImageThumbPath = simpleJsonMovieData.getJSONObject(i).getString("backdrop_path");
                            String vImagePath = simpleJsonMovieData.getJSONObject(i).getString("poster_path");
                            String vPlotSynopsis = simpleJsonMovieData.getJSONObject(i).getString("overview");
                            Double vUserRating = simpleJsonMovieData.getJSONObject(i).getDouble("vote_average");

                            MovieInfo vMovieInfo = new MovieInfo(vOriginalTitle, vReleaseDate, vImageThumbPath, vImagePath, vPlotSynopsis, vUserRating);

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
        } else if (menuItemSelected == R.id.popular_movies){
            movieEndpoint = "/movie/popular?";
            save(getApplicationContext(), "/movie/popular?" );
        }
        //Need to get AsyncTask to run again to fetch new query
        LoaderManager.LoaderCallbacks<MovieInfo[]> callback = MainActivity.this;
        Bundle bundleForLoader = null;
        getSupportLoaderManager().restartLoader(MOVIEDB_QUERY_LOADER, bundleForLoader, callback);
        return super.onOptionsItemSelected(item);
    }

    public void save(Context context, String text){
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(PREFS_KEY, text);
        editor.commit();
    }

    public String getValue(Context context){
        String text;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(PREFS_KEY, null);
        return  text;
    }


}
