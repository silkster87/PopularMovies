package com.example.android.popularmovies;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.popularmovies.data.FavMovieContract;
import com.example.android.popularmovies.data.FavMovieContract.FavMovieEntry;
import com.example.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenMovieJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.net.URL;



//When user clicks on a movie, this will set the view for more info on that movie.
public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding mBinding;
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private int vMovieID;
    private String vOriginalTitle;
    private String vReleaseDate;
    private String vImageThumbPath;
    private String vImagePath;
    private String vPlotSynopsis;
    private String vFirstMovieKey;
    private Double vUserRating;

    private ImageView mMoviePoster;

    private CheckBox mFavMovieCheckBox;
    private RecyclerView mTrailersRecycleView;
    private RecyclerView mReviewsRecycleView;
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private MovieInfo mMovieInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);
        mMovieInfo = getIntent().getParcelableExtra("mMovieDetails");

        mMoviePoster = (ImageView) findViewById(R.id.tv_movie_poster);

        mFavMovieCheckBox = (CheckBox) findViewById(R.id.checkbox_favMovie);

        //Set the Trailers RecyclerView
        mTrailersRecycleView = (RecyclerView) findViewById(R.id.recyclerView_trailers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mTrailersRecycleView.setLayoutManager(layoutManager);
        mTrailersRecycleView.setHasFixedSize(true);
        mTrailersRecycleView.setNestedScrollingEnabled(false);
        mTrailersAdapter = new TrailersAdapter( new TrailersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieTrailerInfo movieTrailerInfo) {
                String baseYoutubeURL = "https://www.youtube.com/watch?v=";
                String movieTrailerURL = baseYoutubeURL + movieTrailerInfo.getvTrailerKey();
                Uri webPage = Uri.parse(movieTrailerURL);
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivity(intent);
                }
            }
        });
        mTrailersRecycleView.setAdapter(mTrailersAdapter);

        //Set the Reviews RecyclerView
        mReviewsRecycleView = (RecyclerView) findViewById(R.id.recyclerView_reviews);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewsRecycleView.setLayoutManager(layoutManager2);
        mReviewsRecycleView.setHasFixedSize(true);
        mReviewsRecycleView.setNestedScrollingEnabled(false);

        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecycleView.setAdapter(mReviewsAdapter);


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

        Context contextPoster = mMoviePoster.getContext();
        Picasso.with(contextPoster).load("http://image.tmdb.org/t/p/w342/" + vImagePath).into(mMoviePoster);

        mBinding.tvMovieTitle.setText(vOriginalTitle);
        mBinding.tvMovieReleaseDate.append(vReleaseDate);
        mBinding.tvMoviePlot.setText(vPlotSynopsis);
        mBinding.tvMovieRating.append(Double.toString(vUserRating));


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

    public void onShareButtonClicked(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi! I'd like to share " + vOriginalTitle + ". Here is the link: "
        + "https://www.youtube.com/watch?v=" + vFirstMovieKey);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    @SuppressLint("StaticFieldLeak")
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
            mTrailersAdapter.setTrailersData(movieTrailerInfos);
            vFirstMovieKey = movieTrailerInfos[0].getvTrailerKey(); //get first key of trailer to use it for sharing

        }
    }

    @SuppressLint("StaticFieldLeak")
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
            //pass the movie reviews array of data to the reviews adapter
            mReviewsAdapter.setReviewsData(movieReviewInfos);
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
               cursor.close();
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
