package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Silky on 07/02/2018.
 */

public class MovieReviewInfo implements Parcelable{

    private final String vID;
    private final String vAuthor;
    private final String vContent;
    private final String vURL;

    public MovieReviewInfo(String id, String author, String content, String url){

        this.vID = id;
        this.vAuthor = author;
        this.vContent = content;
        this.vURL = url;
    }

    public String getvID() {
        return vID;
    }

    public String getvAuthor() {
        return vAuthor;
    }

    public String getvContent() {
        return vContent;
    }

    public String getvURL() {
        return vURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(vID);
        out.writeString(vAuthor);
        out.writeString(vContent);
        out.writeString(vURL);
    }

    private MovieReviewInfo(Parcel in){
        vID = in.readString();
        vAuthor = in.readString();
        vContent = in.readString();
        vURL = in.readString();
    }

    public static final Parcelable.Creator<MovieReviewInfo> CREATOR
            = new Parcelable.Creator<MovieReviewInfo>() {


        @Override
        public MovieReviewInfo createFromParcel(Parcel in) {
            return new MovieReviewInfo(in);
        }

        @Override
        public MovieReviewInfo[] newArray(int size) {
            return new MovieReviewInfo[size];
        }
    };

}
