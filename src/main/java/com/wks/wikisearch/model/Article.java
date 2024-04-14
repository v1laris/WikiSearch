package com.wks.wikisearch.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@Table(name = "article")
@AllArgsConstructor
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

    public Article() {
        // no args constructor
    }
}
