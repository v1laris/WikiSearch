package com.wks.wikisearch.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wks.wikisearch.model.Country;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class AppUserDTO {
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private int age;
}
