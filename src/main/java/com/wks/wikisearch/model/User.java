package com.wks.wikisearch.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Data
@Entity
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    @Column(name = "email", unique = true)
    private String email;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY,
                cascade = {
                    CascadeType.PERSIST,
                    CascadeType.PERSIST})
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    @JsonBackReference
    private Country country;

    @Transient
    private int age;

    public User() {
        // no args constructor
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
