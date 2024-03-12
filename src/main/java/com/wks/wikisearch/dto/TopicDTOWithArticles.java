package com.wks.wikisearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class TopicDTOWithArticles {
    private Long id;
    private String name;
    List<ArticleDTO> articles;
}
