package com.wks.wikisearch.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CountryDTO {
    private Long id;
    private String name;
}
