package com.superconnected.newsreader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.superconnected.newsreader.models.Article;
import com.yahoo.squidb.sql.TableStatement;

import java.io.IOException;
import java.io.Reader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArticleFetcherService extends IntentService {
    private static final String TAG = "ArticleFetcherService";

    // newsapi.org constants
    private static final String NEWS_API_KEY = "90aa26946bea4718a3b603ef70f74fc9";
    private static final String ARTICLE_LIST_URL = String.format("https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=%s", NEWS_API_KEY);

    private static final String ACTION_FETCH_ARTICLES = "com.superconnected.newsreader.action.FETCH_ARTICLES";

    public ArticleFetcherService() {
        super("ArticleFetcherService");
    }

    public static void startFetchArticles(Context context) {
        Intent intent = new Intent(context, ArticleFetcherService.class);
        intent.setAction(ACTION_FETCH_ARTICLES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_ARTICLES.equals(action)) {
                fetchAndStoreArticles();
            }
        }
    }

    private void fetchAndStoreArticles() {
        Reader reader = fetchArticles();
        if (reader == null) {
            Log.e(TAG, "Error fetching articles - got null response");
            return;
        }

        parseAndStoreArticles(reader);
    }

    private void parseAndStoreArticles(Reader articlesReader) {
        JsonReader reader = new JsonReader(articlesReader);

        try {
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("articles".equals(name)) {
                    handleArticlesArray(reader);
                } else {
                    reader.skipValue();
                }
            }

            reader.endObject();
        } catch (IOException e) {
            Log.e(TAG, "Error parsing articles", e);
        }
    }

    private void handleArticlesArray(JsonReader reader) throws IOException {
        reader.beginArray();

        while (reader.hasNext()) {
            parseAndStoreArticle(reader);
        }

        reader.endArray();
    }

    private void parseAndStoreArticle(JsonReader reader) throws IOException {
        reader.beginObject();

        Article article = new Article();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("title".equals(name)) {
                String title = reader.nextString();
                Log.d(TAG, "Title: " + title);
                article.setTitle(title);
            } else if ("description".equals(name)) {
                String description = readOptionalString(reader);
                Log.d(TAG, "Description: " + description);
                article.setDescription(description);
            } else if ("urlToImage".equals(name)) {
                String imageUrl = readOptionalString(reader);
                Log.d(TAG, "Image URL: " + imageUrl);
                article.setImageUrl(imageUrl);
            } else if ("url".equals(name)) {
                String url = readOptionalString(reader);
                Log.d(TAG, "URL: " + url);
                article.setUrl(url);
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();

        // Persist the article
        NewsReaderDatabase database = NewsReaderApplication.getDatabase();
        database.persistWithOnConflict(article, TableStatement.ConflictAlgorithm.IGNORE);
    }

    private String readOptionalString(JsonReader reader) throws IOException {
        JsonToken type = reader.peek();
        if (type == JsonToken.STRING) {
            return reader.nextString();
        } else {
            reader.skipValue();
            return null;
        }
    }

    private Reader fetchArticles() {
        Log.d(TAG, "Fetching articles");

        OkHttpClient client = NewsReaderApplication.getHttpClient();

        Request request = new Request.Builder()
                .url(ARTICLE_LIST_URL)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().charStream();
        } catch (IOException e) {
            Log.e(TAG, "Error fetching artciles", e);
            return null;
        }
    }

}
