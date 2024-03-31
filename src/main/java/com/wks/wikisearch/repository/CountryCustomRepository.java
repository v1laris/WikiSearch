package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Country;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CountryCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    public CountryCustomRepository(final JdbcTemplate dbTemplate) {
        this.jdbcTemplate = dbTemplate;
    }

    public void updateCountry(final Country country) {
        String sql = "UPDATE country SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, country.getName(), country.getId());
    }
}
