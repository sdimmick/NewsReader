package com.superconnected.newsreader;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.superconnected.newsreader.models.Article;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

public class ArticleViewHolder extends SquidViewHolder<Article> {
    private View mItemView;
    private TextView mTitleView;
    private ImageView mImageView;

    public ArticleViewHolder(View itemView) {
        super(itemView, new Article());

        mItemView = itemView;
        mTitleView = itemView.findViewById(R.id.article_title);
        mImageView = itemView.findViewById(R.id.article_image);
    }

    public void bindArticle(final Article article, final Context context) {
        mTitleView.setText(article.getTitle());

        String imageUrl = article.getImageUrl();
        if (imageUrl != null) {
            GlideApp
                    .with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(mImageView);
        }

        // Set article click listener
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArticleActivity.start(context, article.getId());
            }
        });
    }
}
