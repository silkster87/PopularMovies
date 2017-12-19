package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Silky on 15/12/2017.
 * This is a class to create a MovieInfo Object which will be used to display more detail about
 * a movie when it is click on in the main screen.
 */

public class MovieInfo implements Parcelable{

    private String vOriginalTitle;
    private String vImageThumbPath;
    private String vImagePath;
    private String vPlotSynopsis;
    private Double vUserRating;

    public MovieInfo(String originalTitle, String imageThumbPath, String imagePath, String plotSynopsis, Double userRating){

        this.vOriginalTitle = originalTitle;
        this.vImageThumbPath = imageThumbPath;
        this.vImagePath = imagePath;
        this.vPlotSynopsis = plotSynopsis;
        this.vUserRating = userRating;
    }

    public String getvOriginalTitle() {
        return vOriginalTitle;
    }

    public String getvImageThumbPath() {
        return vImageThumbPath;
    }

    public String getvImagePath() { return vImagePath; }

    public String getvPlotSynopsis() { return vPlotSynopsis; }

    public Double getvUserRating() {
        return vUserRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Writing the values to save to the Parcel.
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(vOriginalTitle);
        out.writeString(vImageThumbPath);
        out.writeString(vImagePath);
        out.writeString(vPlotSynopsis);
        out.writeDouble(vUserRating);
    }

    //Retrieve values originally wrote into the Parcel - only CREATOR field can access
    private MovieInfo(Parcel in){
        vOriginalTitle = in.readString();
        vImageThumbPath = in.readString();
        vImagePath = in.readString();
        vPlotSynopsis = in.readString();
        vUserRating = in.readDouble();
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR
            = new Parcelable.Creator<MovieInfo>(){

        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
