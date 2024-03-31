package com.wks.wikisearch.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import lombok.Data;

@Data
@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "topics", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();

    public Topic(final Long newId, final String newName) {
        this.id = newId;
        this.name = newName;
        this.articles = new HashSet<>();
    }

    public Topic() {

    }
}
