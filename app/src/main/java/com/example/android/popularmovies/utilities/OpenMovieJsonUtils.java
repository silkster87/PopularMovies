package com.example.android.popularmovies.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**This class gets the String results of the api query into a JSONArray
 * Created by Silky on 11/12/2017.
 */

public class OpenMovieJsonUtils {
    private static final String MESSAGE_CODE = "cod" ;

    //This method will just get the whole JSON array of results

    public static JSONArray getJsonArrayOfMovieResults(Context context, String jsonMovieDataResponse) throws JSONException{

        JSONArray parsedMovieResultsData;

        JSONObject movieJSON = new JSONObject(jsonMovieDataResponse);

        //This checks if there is an error code.
        if(movieJSON.has(MESSAGE_CODE)){
            int errorCode = movieJSON.getInt(MESSAGE_CODE);

            switch(errorCode){
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        parsedMovieResultsData = movieJSON.getJSONArray("results");

        return parsedMovieResultsData;
    }
}
