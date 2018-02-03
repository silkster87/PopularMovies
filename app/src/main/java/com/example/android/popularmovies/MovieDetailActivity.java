package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavMovieContract;
import com.squareup.picasso.Picasso;

//When user clicks on a movie, this will set the view for more info on that movie.
public class MovieDetailActivity extends AppCompatActivity {

    private SQLiteDatabase db;

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
    }

    public void onFavCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        if(checked){
            //add movie to favourite movie DB
            addFavMovie(vMovieID, vOriginalTitle, vReleaseDate, vImageThumbPath, vImagePath, vPlotSynopsis, vUserRating);

        } else{
            //delete movie from database
        }
    }

    private long addFavMovie(int movieID, String originalTitle, String releaseDate, String imageThumbPath,
                             String imagePath, String plotSynopsis, double userRating){

        ContentValues cv = new ContentValues();

        cv.put(FavMovieContract.FavMovieEntry.MOVIE_ID, movieID);
        cv.put(FavMovieContract.FavMovieEntry.ORIGINAL_TITLE, originalTitle);
        cv.put(FavMovieContract.FavMovieEntry.RELEASE_DATE, releaseDate);
        cv.put(FavMovieContract.FavMovieEntry.MOVIE_IMAGE_THUMB_PATH, imageThumbPath);
        cv.put(FavMovieContract.FavMovieEntry.MOVIE_IMAGE_PATH, imagePath);
        cv.put(FavMovieContract.FavMovieEntry.PLOT_SYNOPSIS, plotSynopsis);
        cv.put(FavMovieContract.FavMovieEntry.USER_RATING, userRating);

        return db.insert(FavMovieContract.FavMovieEntry.TABLE_NAME, null, cv);

    }
}
