package com.wks.wikisearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleDTOWithTopics {
    private Long id;
    private String title;
    private String url;
    private List<TopicDTO> topics;
}
