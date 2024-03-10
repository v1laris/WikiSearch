package com.wks.wikisearch.model;

import jakarta.persistence.*;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST}, mappedBy = "topics", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();

    public Topic(Long id, String name) {
        this.id = id;
        this.name = name;
        this.articles = new HashSet<>();
    }

    public Topic() {

    }
}
