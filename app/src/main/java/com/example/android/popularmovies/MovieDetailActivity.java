package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import com.example.android.popularmovies.data.FavMovieDbHelper;
import com.squareup.picasso.Picasso;

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
               Toast.makeText(getBaseContext(), "Not in fav movie DB", Toast.LENGTH_LONG).show();
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
