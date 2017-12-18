package com.example.demondrelivingston.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {


    /**
     * URL for book data from the Google Book Api dataset
     */
    private static String Book_Request_URL =
            "https://www.googleapis.com/books/v1/volumes?maxResults=20&q=";

    /**
     * Constant value for the Book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int Book_Loader_ID = 1;

    private BookAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private String BOOK_RESULTS = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting an TextChanged Listener to find books that are being looked up
        final EditText search = (EditText) findViewById(R.id.search_bar);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextView notStarted = (TextView) findViewById(R.id.empty_view);
                notStarted.setText(R.string.favorite_title);
            }

            //used to start the search over each time you changed something in the search bar
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Book_Request_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=20&q=";

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Book_Request_URL += search.getText().toString().trim().toLowerCase();
            }
        });

        final Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkConnected()) {
                    String searchQuery = search.getText().toString().replaceAll(" ", "+");
                    if (searchQuery != null && !searchQuery.equals("")) {
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No internet is connected", Toast.LENGTH_LONG);
                    toast.show();

                }
            }
        });

        //Find a reference to the ListView in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        //Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        //Set the adapter on the ListView so the list can be populated in the user inference
        bookListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected())

        {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(Book_Loader_ID, null, this);
        } else

        {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    private String getUrlForHttpRequest() {
        return Book_Request_URL;
    }

    private void updateUi(List<Book> books) {
        if (books.isEmpty()) {
            // if no books found, show a message
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
        }
        mAdapter.clear();
        mAdapter.addAll(books);
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(URL... urls) {
            URL url = QueryUtils.createUrl(getUrlForHttpRequest());
            String jsonResponse = "";

            try {
                jsonResponse = QueryUtils.makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }


            List<Book> books = parseJSON(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }
    }

    private List<Book> parseJSON(String json) {
        if (json == null) {
            return null;
        }
        return QueryUtils.extractFeatureFromJson(json);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        return new BookLoader(this, Book_Request_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);
        //Set the empty state text to display "No Books Found."
        mEmptyStateTextView.setText(R.string.no_books);

        //Clear the adapter of previous book data
        mAdapter.clear();

        //If there is a valid list of books, then add them to the data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

    }

    private boolean NetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}