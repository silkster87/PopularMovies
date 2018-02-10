package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Silky on 10/02/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {


    private MovieTrailerInfo[] mMovieTrailerInfo;
    private final OnItemClickListener listener;

    public TrailersAdapter(MovieTrailerInfo[] movieTrailers, OnItemClickListener listener){
        this.mMovieTrailerInfo = movieTrailers;
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(MovieTrailerInfo movieTrailerInfo);
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        Boolean shouldattachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldattachToParentImmediately);

        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position) {
        holder.bind(mMovieTrailerInfo[position], listener);

        String trailerName = mMovieTrailerInfo[position].getvTrailerName();
        holder.mTrailerButton.setText(trailerName);
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder{
        public final Button mTrailerButton;

        public TrailersAdapterViewHolder(View view){
            super(view);
            mTrailerButton = view.findViewById(R.id.trailer_button);
        }

        public void bind(final MovieTrailerInfo movieTrailer, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    listener.onItemClick(movieTrailer);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        if(mMovieTrailerInfo == null) return 0;
        return mMovieTrailerInfo.length;
    }

    public void setTrailersData(MovieTrailerInfo[] data){
        mMovieTrailerInfo = data;
        notifyDataSetChanged();
    }


}
