package com.wks.wikisearch.dto;

import com.wks.wikisearch.model.Country;
import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppUserDTOWithCountry {
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private CountryDTO country;
    private int age;
}
