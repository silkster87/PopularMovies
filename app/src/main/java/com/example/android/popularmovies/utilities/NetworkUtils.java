package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Silky on 07/12/2017.
 * NetworkUtils will be used to communicate with the network
 */

public class NetworkUtils {

    //API key is in the MOVIEDB_BASE_URL

    private final static String MOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3";

    //API Key is in the BuildConfig file which I have told Git to ignore to protect it
    private final static String API_KEY =
            BuildConfig.API_KEY;


    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static URL buildUrl(String endpoint){

        if (endpoint == null){
            endpoint = "/movie/popular?"; //By default set it to popular movies
        }

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL +
                endpoint + "api_key=" + API_KEY).buildUpon().build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI" + url);

        return url;

    }


    //getResponseFromHttpUrl will return the result from http of the url

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
