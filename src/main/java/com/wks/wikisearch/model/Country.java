package com.wks.wikisearch.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.List;

@Data
@Entity
@AllArgsConstructor
public class Country {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String name;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "country",
            cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<User> countryUsers;

    public Country() {
        // no args constructor
    }
}
