package com.example.demondrelivingston.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by demondrelivingston on 12/14/17.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    /**
     * Query URL
     */
    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        //Perform the network request, parse the response, and extract a list of books
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}
