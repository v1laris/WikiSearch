package com.wks.wikisearch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Country {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}
