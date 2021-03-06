package com.example.android.p6newsappstage1;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity
        implements LoaderCallbacks<List<com.example.android.p6newsappstage1.Article>> {

    private static final String LOG_TAG = ArticleActivity.class.getName();

    /** URL for newsfeed data from the Guardian JSON dataset */
    private static final String GUARDIAN_REQUEST_URL =
            "http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&page-size=15&q=politics&api-key=f1dfc1ea-9071-49cc-b586-005ed71ac92c";

    /**
     * Constant value for the article loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int JSON_ARTICLE_FEED_LOADER_ID = 1;



    /** Adapter for the list of news */
    private ArticleAdapter JSONnewsAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articles_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsfeedListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsfeedListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        JSONnewsAdapter = new ArticleAdapter(this, new ArrayList<com.example.android.p6newsappstage1.Article>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsfeedListView.setAdapter(JSONnewsAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsfeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                com.example.android.p6newsappstage1.Article currentArticle = JSONnewsAdapter.getItem(position);
                Uri newsfeedUrl = Uri.parse(currentArticle.getUrl());
                // View the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsfeedUrl);
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(JSON_ARTICLE_FEED_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<com.example.android.p6newsappstage1.Article>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new ArticleLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<com.example.android.p6newsappstage1.Article>> loader, List<com.example.android.p6newsappstage1.Article> articles) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No articles found."
        mEmptyStateTextView.setText(R.string.no_article);

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            //JSONnewsAdapter.addAll(articles);
            JSONnewsAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<com.example.android.p6newsappstage1.Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        JSONnewsAdapter.clear();
    }
}
