package com.wks.wikisearch.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Topic {
    private int topicId; // Уникальный идентификатор темы
    private String name; // Название темы
}
