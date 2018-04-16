package com.superconnected.newsreader;

import android.app.Application;

import okhttp3.OkHttpClient;

public class NewsReaderApplication extends Application {
    private static NewsReaderApplication sInstance;
    private static OkHttpClient sOkHttpClient;
    private static NewsReaderDatabase sDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        sOkHttpClient = new OkHttpClient();
        sDatabase = new NewsReaderDatabase();
    }

    public static NewsReaderApplication getInstance() {
        return sInstance;
    }

    public static OkHttpClient getHttpClient() {
        return sOkHttpClient;
    }

    public static NewsReaderDatabase getDatabase() {
        return sDatabase;
    }

}
