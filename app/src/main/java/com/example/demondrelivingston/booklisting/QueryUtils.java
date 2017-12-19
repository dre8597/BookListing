package com.example.demondrelivingston.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by demondrelivingston on 12/14/17.
 */

public final class QueryUtils {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    QueryUtils() {

    }

    public static StringBuilder ListOfAuthors(JSONArray authorsList) throws JSONException {

        StringBuilder authorsListInString = null;

        if (authorsList.length() == 0) {
            return null;
        }

        for (int i = 0; i < authorsList.length(); i++) {
            if (i == 0) {
                authorsListInString = new StringBuilder(authorsList.getString(0));
            } else {
                authorsListInString.append(", ").append(authorsList.getString(i));
            }
        }

        return authorsListInString;
    }

    public static List<Book> fetchBookData(String requestUrl) {
        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Create URL object
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            e.printStackTrace();
        }


        List<Book> books = extractFeatureFromJson(jsonResponse);
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = QueryUtils.readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<Book> extractFeatureFromJson(String BookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(BookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(BookJSON);

            if (baseJsonResponse.has("items")) {
            // Extract the JSONArray associated with the key called "items",
            // which represents a list of features (or Books).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");
                // For each book in the bookArray, create an {@link Books} object
                for (int i = 0; i < bookArray.length(); i++) {

                    // Get a single book at position i within the list of Books
                    JSONObject currentBook = bookArray.getJSONObject(i);

                    // For a given Book, extract the JSONObject associated with the
                    // key called "volumeInfo", which represents a list of all volumeInfo
                    // for that book.
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    // Extract the value for the key called "title"
                    String title = volumeInfo.getString("title");

                    //  String author = volumeInfo.getString("authors");

                    StringBuilder authorArray = new StringBuilder("");
                    // Extract the value for the key called "author"
                    if (volumeInfo.has("authors")) {
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        authorArray = ListOfAuthors(authors);
                    } else {
                        authorArray.append("");

                    }


                    // Create a new {@link Book} object with the magnitude, location, time,
                    // and url from the JSON response.
                    Book book = new Book(title, authorArray.toString());

                    // Add the new {@link Book} to the list of Books.
                    books.add(book);
                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Book JSON results", e);
        }

        // Return the list of Books
        return books;
    }

}
