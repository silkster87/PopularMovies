package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Silky on 10/02/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private MovieReviewInfo[] mMovieReviews;

    public ReviewsAdapter(){

    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.reviews_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        Boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
            String reviewAuthor = mMovieReviews[position].getvAuthor();
            String reviewContent = mMovieReviews[position].getvContent();

            holder.mReviewAuthor.setText(reviewAuthor);
            holder.mReviewContent.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        if(mMovieReviews == null) return 0;
        return mMovieReviews.length;
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mReviewAuthor = itemView.findViewById(R.id.review_author);
            mReviewContent = itemView.findViewById(R.id.review_content);
        }
    }

    public void setReviewsData(MovieReviewInfo[] data){
        mMovieReviews = data;
        notifyDataSetChanged();
    }
}
