package com.example.android.popularmovies;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavMovieContract;
import com.example.android.popularmovies.data.FavMovieContract.FavMovieEntry;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenMovieJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.net.URL;

//When user clicks on a movie, this will set the view for more info on that movie.
public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private int vMovieID;
    private String vOriginalTitle;
    private String vReleaseDate;
    private String vImageThumbPath;
    private String vImagePath;
    private String vPlotSynopsis;
    private Double vUserRating;

    private ImageView mMovieThumbnail;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMoviePlot;
    private TextView mMovieRating;
    private CheckBox mFavMovieCheckBox;

    private MovieInfo mMovieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mMovieInfo = getIntent().getParcelableExtra("mMovieDetails");

        mMovieThumbnail = (ImageView) findViewById(R.id.tv_movie_thumbnail);
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mMoviePlot = (TextView) findViewById(R.id.tv_movie_plot);
        mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        mFavMovieCheckBox = (CheckBox) findViewById(R.id.checkbox_favMovie);

        //find out if this particular movie has been added to favourite movies database if it is
        //then the mFavMovieCheckBox needs to be checked

        //Get the data from the MovieInfo object from parcelable
        vMovieID = mMovieInfo.getvMovieID();
        vOriginalTitle = mMovieInfo.getvOriginalTitle();
        vReleaseDate = mMovieInfo.getvReleaseDate();
        vImageThumbPath = mMovieInfo.getvImageThumbPath();
        vImagePath = mMovieInfo.getvImagePath();
        vPlotSynopsis = mMovieInfo.getvPlotSynopsis();
        vUserRating = mMovieInfo.getvUserRating();

        //Update the text/image views on the secondary activity

        Context context = mMovieThumbnail.getContext();
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + vImageThumbPath).into(mMovieThumbnail);
        mMovieTitle.setText(vOriginalTitle);
        mMovieReleaseDate.append(vReleaseDate);
        mMoviePlot.append(vPlotSynopsis);
        mMovieRating.append(Double.toString(vUserRating));

        boolean isFavMovie = findIfFavMovie();

        if(isFavMovie){
            mFavMovieCheckBox.setChecked(true);
        }else{
            mFavMovieCheckBox.setChecked(false);
        }
        runMultipleAsyncTask();

    }

    private void runMultipleAsyncTask() {

        MovieTrailerTask movieTrailerTask = new MovieTrailerTask();
        String movieID = Integer.toString(vMovieID);
        String movieTrailerEndpoint = "/movie/" + movieID + "/videos?";
        URL movieTrailersURL = NetworkUtils.buildUrl(movieTrailerEndpoint);
        movieTrailerTask.execute(movieTrailersURL);

        MovieReviewTask movieReviewTask = new MovieReviewTask();
        String movieReviewEndpoint = "/movie/" + movieID + "/reviews?";
        URL movieReviewsURL = NetworkUtils.buildUrl(movieReviewEndpoint);
        movieReviewTask.execute(movieReviewsURL);
    }

    private class MovieTrailerTask extends AsyncTask<URL, Void, MovieTrailerInfo[]> {


        @Override
        protected MovieTrailerInfo[] doInBackground(URL... urls) {

            URL movieTrailersURL = urls[0];

            try{
                String jsonMovieTrailerDataResponse =
                        NetworkUtils.getResponseFromHttpUrl(movieTrailersURL);

                JSONArray simpleJsonMovieTrailersData =
                        OpenMovieJsonUtils.getJsonArrayOfMovieResults(MovieDetailActivity.this, jsonMovieTrailerDataResponse);

                if(simpleJsonMovieTrailersData == null) throw new AssertionError();
                MovieTrailerInfo[] movieTrailersInfoArray = new MovieTrailerInfo[simpleJsonMovieTrailersData.length()];

                for(int i=0; i<simpleJsonMovieTrailersData.length(); i++){
                    String movieTrailerID = simpleJsonMovieTrailersData.getJSONObject(i).getString("id");
                    String movieTrailerName = simpleJsonMovieTrailersData.getJSONObject(i).getString("name");
                    String movieTrailerKey = simpleJsonMovieTrailersData.getJSONObject(i).getString("key");

                    MovieTrailerInfo movieTrailerInfo = new MovieTrailerInfo(movieTrailerID, movieTrailerName, movieTrailerKey);
                    movieTrailersInfoArray[i] = movieTrailerInfo;
                }
                return movieTrailersInfoArray;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(MovieTrailerInfo[] movieTrailerInfos) {
            super.onPostExecute(movieTrailerInfos);
            //pass the movie trailers array to the trailers recycle view adapter
        }
    }

    private class MovieReviewTask extends AsyncTask<URL, Void, MovieReviewInfo[]>{


        @Override
        protected MovieReviewInfo[] doInBackground(URL... urls) {

            URL movieReviewsURL = urls[0];
            try{
                String jsonMovieReviewDataResponse =
                        NetworkUtils.getResponseFromHttpUrl(movieReviewsURL);

                JSONArray simpleJsonMovieReviewsData =
                        OpenMovieJsonUtils.getJsonArrayOfMovieResults(MovieDetailActivity.this, jsonMovieReviewDataResponse);

                if(simpleJsonMovieReviewsData==null) throw new AssertionError();
                MovieReviewInfo[] movieReviewsInfoArray = new MovieReviewInfo[simpleJsonMovieReviewsData.length()];

                for(int i=0; i<simpleJsonMovieReviewsData.length(); i++){

                    String movieReviewID = simpleJsonMovieReviewsData.getJSONObject(i).getString("id");
                    String movieReviewAuthor = simpleJsonMovieReviewsData.getJSONObject(i).getString("author");
                    String movieReviewContent = simpleJsonMovieReviewsData.getJSONObject(i).getString("content");
                    String movieReviewURL = simpleJsonMovieReviewsData.getJSONObject(i).getString("url");

                    MovieReviewInfo movieReviewInfo = new MovieReviewInfo(movieReviewID, movieReviewAuthor, movieReviewContent, movieReviewURL);
                    movieReviewsInfoArray[i] = movieReviewInfo;
                }
                return movieReviewsInfoArray;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieReviewInfo[] movieReviewInfos) {
            super.onPostExecute(movieReviewInfos);
            //pass the movie reviews array of data to the review recycle view adapter
        }
    }


    //This method finds out if the movie displayed is in the fav Movie database
    private boolean findIfFavMovie() {

        try{
            String mSelection = FavMovieContract.FavMovieEntry.MOVIE_ID + "=?";
           Cursor cursor = getContentResolver().query(FavMovieEntry.CONTENT_URI, null,
                   mSelection,
                   new String[]{Integer.toString(vMovieID)},
                   null,null);
           if(cursor.getCount() != 0) {
               return true;
           } else {
               return false;
           }
        } catch (Exception e){
            Toast.makeText(getBaseContext(), "query was wrong", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Unable to query data or not favourited movie.");
            e.printStackTrace();
            return false;
        }
    }

    public void onFavCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        if(checked){
            //add movie to favourite movie DB
            addFavMovie(vMovieID, vOriginalTitle, vReleaseDate, vImageThumbPath, vImagePath, vPlotSynopsis, vUserRating);

        } else{
            //delete movie from database
            int itemsDeleted = getContentResolver().delete(FavMovieEntry.CONTENT_URI, "MOVIE_ID=?",
                    new String[]{Integer.toString(vMovieID)});

            if(itemsDeleted != 0){
                Toast.makeText(getBaseContext(), vOriginalTitle + " removed from favourites.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    private void addFavMovie(int movieID, String originalTitle, String releaseDate, String imageThumbPath,
                             String imagePath, String plotSynopsis, double userRating){

        ContentValues cv = new ContentValues();

        cv.put(FavMovieEntry.MOVIE_ID, movieID);
        cv.put(FavMovieEntry.ORIGINAL_TITLE, originalTitle);
        cv.put(FavMovieEntry.RELEASE_DATE, releaseDate);
        cv.put(FavMovieEntry.MOVIE_IMAGE_THUMB_PATH, imageThumbPath);
        cv.put(FavMovieEntry.MOVIE_IMAGE_PATH, imagePath);
        cv.put(FavMovieEntry.PLOT_SYNOPSIS, plotSynopsis);
        cv.put(FavMovieEntry.USER_RATING, Double.toString(userRating));

        ContentResolver resolver = getContentResolver();

        Uri insertedUri = resolver.insert(FavMovieEntry.CONTENT_URI, cv);

        if(insertedUri != null){
            Toast.makeText(getBaseContext(), originalTitle + " added to Favourites. ", Toast.LENGTH_LONG).show();
        }

    }

}
