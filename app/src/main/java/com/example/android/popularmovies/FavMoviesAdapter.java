package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Silky on 04/02/2018.
 */

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavMoviesAdapterViewHolder>{

    private MovieInfo[] mFavMovieData;
    private final Context mContext;

    private final OnFavMovieItemClickListener listener;

    public FavMoviesAdapter(Context mContext, OnFavMovieItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public interface OnFavMovieItemClickListener {
        void onItemClick(MovieInfo movieInfo);
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

    @Override
    public void onBindViewHolder(FavMoviesAdapter.FavMoviesAdapterViewHolder holder, int position) {
        holder.bind(mFavMovieData[position], listener);

        String moviePicture = mFavMovieData[position].getvImagePath();

        Context context = holder.mFavMovieImageView.getContext();
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + moviePicture)
                .into(holder.mFavMovieImageView);
    }

    @Override
    public int getItemCount() {
        if(null == mFavMovieData) return 0;
        return mFavMovieData.length;
    }

    public class FavMoviesAdapterViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mFavMovieImageView;

        public FavMoviesAdapterViewHolder(View view) {
            super(view);
            mFavMovieImageView = view.findViewById(R.id.tv_fav_movie_pic);
        }

        public void bind(final MovieInfo mFavMovieInfo, final OnFavMovieItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(mFavMovieInfo);
                }
            });
        }
    }
}
