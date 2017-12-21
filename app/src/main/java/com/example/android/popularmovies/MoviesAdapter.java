package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;


/**
 * Created by Silky on 08/12/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private MovieInfo[] mMovieData;
    private final OnItemClickListener listener;

    public MoviesAdapter(MovieInfo[] items, OnItemClickListener listener) {
        this.mMovieData = items;
        this.listener = listener;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movies_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        holder.bind(mMovieData[position], listener);

        //The moviePicture string contains the relative path to the picture to upload
        String moviePicture = mMovieData[position].getvImagePath();

        Context context = holder.mMovieImageView.getContext();
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + moviePicture).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.length;
    }

    //implementing a click listener which will be put into constructor that handles Movie object
    public interface OnItemClickListener {
        void onItemClick(MovieInfo items);
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        //   public final TextView mMovieTextView;
        public final ImageView mMovieImageView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            //   mMovieTextView = view.findViewById(R.id.tv_movie_data);
            mMovieImageView = view.findViewById(R.id.tv_movie_pic);
        }

        public void bind(final MovieInfo movieInfo, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movieInfo);
                    //When user clicks on movie image we will open new activity in onItemClick method.
                }
            });
        }
    }

    public void setMovieData(MovieInfo[] MovieData) {
        mMovieData = MovieData;
        notifyDataSetChanged();
    }
}
