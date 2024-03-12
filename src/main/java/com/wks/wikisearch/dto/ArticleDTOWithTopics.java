package com.wks.wikisearch.dto;

import com.wks.wikisearch.model.Topic;
import lombok.Data;

import java.util.List;

@Data
public class ArticleDTOWithTopics {
    private Long id;
    private String title;
    private String url;
    List<TopicDTO> topics;
}