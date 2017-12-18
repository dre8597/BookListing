package com.example.demondrelivingston.booklisting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by demondrelivingston on 12/14/17.
 */

public class Book implements Parcelable {

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

    private Book(Parcel in) {
        mAuthor = in.readString();
        mTitle = in.readString();
    }
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mAuthor);
    }
}
