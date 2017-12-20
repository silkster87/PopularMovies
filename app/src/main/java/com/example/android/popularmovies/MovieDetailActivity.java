package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

//When user clicks on a movie, this will set the view for more info on that movie.
public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mMovieThumbnail;
    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMoviePlot;
    private TextView mMovieRating;

    private String vOriginalTitle;
    private String vReleaseDate;
    private String vImageThumbPath;
    private String vImagePath;
    private String vPlotSynopsis;
    private Double vUserRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        MovieInfo mMovieInfo = getIntent().getParcelableExtra("mMovieDetails");

        mMovieThumbnail = (ImageView) findViewById(R.id.tv_movie_thumbnail);
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
        mMoviePlot = (TextView) findViewById(R.id.tv_movie_plot);
        mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);

        //Get the data from the MovieInfo object from parcelable
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
}
