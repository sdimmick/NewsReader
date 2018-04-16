package com.superconnected.newsreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.superconnected.newsreader.models.Article;
import com.yahoo.squidb.recyclerview.SquidRecyclerAdapter;

public class ArticleListAdapter extends SquidRecyclerAdapter<Article, ArticleViewHolder> {
    private Context mContext;

    public ArticleListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void onBindSquidViewHolder(ArticleViewHolder holder, int position) {
        Article article = holder.item;
        holder.bindArticle(article, mContext);
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_item, parent, false);
        return new ArticleViewHolder(view);
    }
}
