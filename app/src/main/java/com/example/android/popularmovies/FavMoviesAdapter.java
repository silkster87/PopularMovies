package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Silky on 05/02/2018.
 */

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavMoviesAdapterViewHolder>{

    private final Context mContext;
    private MovieInfo[] mMovieData;
    private Cursor mCursor;

    private final OnFavMovieItemClickListener listener;


    public FavMoviesAdapter(Context context, OnFavMovieItemClickListener listener){
        this.mContext = context;
        this.listener = listener;

    }

    @Override
    public FavMoviesAdapter.FavMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       Context context = parent.getContext();
       int layoutIdForFavMovieItem = R.layout.fav_movies_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForFavMovieItem, parent, shouldAttachToParentImmediately);

        return new FavMoviesAdapterViewHolder(view);
    }

    public interface OnFavMovieItemClickListener{
        void onItemClick(MovieInfo movieInfo);
    }

    @Override
    public void onBindViewHolder(FavMoviesAdapter.FavMoviesAdapterViewHolder holder, int position) {
        holder.bind(mMovieData[position], listener);
        String moviePicture = mMovieData[position].getvImagePath();

        Context context = holder.mFavMovieImageView.getContext();
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + moviePicture)
                .into(holder.mFavMovieImageView);

    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        putCursorDataIntoMovieInfo(mCursor);
        notifyDataSetChanged();
    }

    private void putCursorDataIntoMovieInfo(Cursor mCursor) {

        int noOfRows = mCursor.getCount();
        mMovieData = new MovieInfo[noOfRows];
        mCursor.moveToFirst();
        for(int i=1; i <= noOfRows ; i++){
            int movieID = mCursor.getInt(FavMoviesActivity.INDEX_MOVIE_ID);
            String originalTitle = mCursor.getString(FavMoviesActivity.INDEX_ORIGINAL_TITLE);
            String releaseDate = mCursor.getString(FavMoviesActivity.INDEX_RELEASE_DATE);
            String imageThumbPath = mCursor.getString(FavMoviesActivity.INDEX_MOVIE_IMAGE_THUMB_PATH);
            String imagePath = mCursor.getString(FavMoviesActivity.INDEX_MOVIE_IMAGE_PATH);
            String plot = mCursor.getString(FavMoviesActivity.INDEX_MOVIE_PLOT_SYNOPSIS);
            Double userRating = mCursor.getDouble(FavMoviesActivity.INDEX_USER_RATING);

            MovieInfo movieInfo = new MovieInfo(movieID, originalTitle, releaseDate, imageThumbPath, imagePath, plot, userRating);
            mMovieData[i-1] = movieInfo;
            mCursor.moveToNext();
        }
    }

    @Override
    public int getItemCount() {
        if(null == mCursor) return 0;
        return mCursor.getCount();
    }

    public class FavMoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mFavMovieImageView;

        public FavMoviesAdapterViewHolder(View view) {
            super(view);
            mFavMovieImageView = view.findViewById(R.id.tv_fav_movie_pic);
        }

        public void bind(final MovieInfo movieInfo, final OnFavMovieItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(movieInfo);
                }
            });
        }
    }
}
