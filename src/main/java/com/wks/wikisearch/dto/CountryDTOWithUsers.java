package com.wks.wikisearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class CountryDTOWithUsers {
    private Long id;
    private String name;
    private List<UserDTO> users;
}
