package com.superconnected.newsreader.models;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className = "Article", tableName = "article")
public class ArticleSpec {
    String title;
    String description;
    String imageUrl;

    @ColumnSpec(constraints = "UNIQUE")
    String url;
}
