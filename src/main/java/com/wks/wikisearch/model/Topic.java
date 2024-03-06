package com.wks.wikisearch.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.Data;

@Data
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "topics")
    private Set<Article> articles = new HashSet<>();
}
