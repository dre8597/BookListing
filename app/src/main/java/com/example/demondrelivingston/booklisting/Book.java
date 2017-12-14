package com.example.demondrelivingston.booklisting;

/**
 * Created by demondrelivingston on 12/14/17.
 */

public class Book {

    /**
     * Title of book
     */
    private String mTitle;

    /**
     * Name of Author
     */
    private String mAuthor;

    public Book(String title, String author) {
        mTitle = title;
        mAuthor = author;

    }

    /**
     * Returns the title of the book
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the name of the author
     */
    public String getAuthor() {
        return mAuthor;
    }
}
