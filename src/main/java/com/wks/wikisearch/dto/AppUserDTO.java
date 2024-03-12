package com.wks.wikisearch.dto;

import lombok.Data;

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
