package com.superconnected.newsreader;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.superconnected.newsreader.models.Article;
import com.yahoo.squidb.android.SquidCursorLoader;
import com.yahoo.squidb.data.SimpleDataChangedNotifier;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

public class ArticleListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SquidCursor<Article>> {
    private RecyclerView mRecyclerView;
    private ArticleListAdapter mAdapter;
    private SimpleDataChangedNotifier mArticlesChangedNotifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mRecyclerView = findViewById(R.id.article_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ArticleListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        ArticleFetcherService.startFetchArticles(this);
        getLoaderManager().initLoader(0, null, this);

        mArticlesChangedNotifier = new SimpleDataChangedNotifier(Article.TABLE) {
            @Override
            protected void onDataChanged() {
                getLoaderManager().restartLoader(0, null, ArticleListActivity.this);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        NewsReaderApplication.getDatabase().registerDataChangedNotifier(mArticlesChangedNotifier);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NewsReaderApplication.getDatabase().unregisterDataChangedNotifier(mArticlesChangedNotifier);
    }

    @Override
    public Loader<SquidCursor<Article>> onCreateLoader(int i, Bundle bundle) {
        Query query = Query.select(Article.PROPERTIES);
        return new SquidCursorLoader<>(this, NewsReaderApplication.getDatabase(), Article.class, query);
    }

    @Override
    public void onLoadFinished(Loader<SquidCursor<Article>> loader, SquidCursor<Article> articleSquidCursor) {
        mAdapter.swapCursor(articleSquidCursor);
    }

    @Override
    public void onLoaderReset(Loader<SquidCursor<Article>> loader) {
        mAdapter.swapCursor(null);
    }
}
