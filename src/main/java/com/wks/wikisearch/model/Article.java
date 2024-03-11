package com.wks.wikisearch.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.Data;

@Data
@Entity
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "article_topic",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<Topic> topics = new HashSet<>();

    public Article(Long id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.topics = new HashSet<>();
    }

    public Article() {

    }
}
