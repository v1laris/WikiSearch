package com.wks.wikisearch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Article {
    private int articleId; // Уникальный идентификатор статьи
    private String title; // Заголовок статьи
    private String content; // Содержание статьи

}
