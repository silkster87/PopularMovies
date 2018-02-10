package com.example.android.popularmovies;

/**Custom class for the movie trailer info
 * Created by Silky on 08/02/2018.
 */

public class MovieTrailerInfo {

    private final String vTrailerID;
    private final String vTrailerName;
    private final String vTrailerKey;

    public MovieTrailerInfo(String vTrailerID, String vTrailerName, String vTrailerKey) {
        this.vTrailerID = vTrailerID;
        this.vTrailerName = vTrailerName;
        this.vTrailerKey = vTrailerKey;
    }


    public String getvTrailerName() {
        return vTrailerName;
    }

    public String getvTrailerKey() {
        return vTrailerKey;
    }
}
