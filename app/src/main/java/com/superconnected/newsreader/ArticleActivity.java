package com.superconnected.newsreader;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.superconnected.newsreader.models.Article;
import com.yahoo.squidb.android.SquidCursorLoader;
import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Query;

public class ArticleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<SquidCursor<Article>> {
    private static final String TAG = "ArticleActivity";
    private static final String ARG_ARTICLE_ID = "articleId";

    private ImageView mArticleImageView;
    private TextView mArticleTitleView;
    private TextView mArticleDescriptionView;

    public static void start(Context context, long articleId) {
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra(ARG_ARTICLE_ID, articleId);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);
        mArticleImageView = findViewById(R.id.article_image);
        mArticleTitleView = findViewById(R.id.article_title);
        mArticleDescriptionView = findViewById(R.id.article_description);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<SquidCursor<Article>> onCreateLoader(int i, Bundle bundle) {
        long articleId = getIntent().getLongExtra(ARG_ARTICLE_ID, -1);
        Log.d(TAG, "Fetching article with id " + articleId);

        Query query = Query.select(Article.PROPERTIES).where(Article.ID.eq(articleId));
        return new SquidCursorLoader<>(this, NewsReaderApplication.getDatabase(), Article.class, query);
    }

    @Override
    public void onLoadFinished(Loader<SquidCursor<Article>> loader, SquidCursor<Article> articleSquidCursor) {
        if (!articleSquidCursor.moveToFirst()) {
            return;
        }

        String title = articleSquidCursor.get(Article.TITLE);
        mArticleTitleView.setText(title);

        String description = articleSquidCursor.get(Article.DESCRIPTION);
        mArticleDescriptionView.setText(description);

        String imageUrl = articleSquidCursor.get(Article.IMAGE_URL);
        GlideApp
                .with(this)
                .load(imageUrl)
                .into(mArticleImageView);
    }

    @Override
    public void onLoaderReset(Loader<SquidCursor<Article>> loader) {

    }
}
