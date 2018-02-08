package com.example.android.popularmovies;

/**
 * Created by Silky on 08/02/2018.
 */

public class MovieTrailerInfo {

    private String vTrailerID;
    private String vTrailerName;
    private String vTrailerKey;

    public MovieTrailerInfo(String vTrailerID, String vTrailerName, String vTrailerKey) {
        this.vTrailerID = vTrailerID;
        this.vTrailerName = vTrailerName;
        this.vTrailerKey = vTrailerKey;
    }

    public String getvTrailerID() {
        return vTrailerID;
    }

    public String getvTrailerName() {
        return vTrailerName;
    }

    public String getvTrailerKey() {
        return vTrailerKey;
    }
}
