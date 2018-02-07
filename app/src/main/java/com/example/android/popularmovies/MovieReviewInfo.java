package com.example.android.popularmovies;

/**
 * Created by Silky on 07/02/2018.
 */

public class MovieReviewInfo {

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
}
