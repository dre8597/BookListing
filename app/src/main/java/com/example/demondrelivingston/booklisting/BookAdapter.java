package com.example.demondrelivingston.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by demondrelivingston on 12/14/17.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book>books) {
        super(context, 0, books);
    }

    // Check if there is an existing list item view (called convertView) that we can reuse,
    // otherwise, if convertView is null, then inflate a new list item layout.
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);
        }
        //Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        //Find the TextView with the view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentBook.getTitle());

        //Find the TextView with the view ID author
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        authorView.setText(currentBook.getAuthor());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}

