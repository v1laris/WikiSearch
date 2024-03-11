package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.model.Article;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppUserCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    public AppUserCustomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateUser(AppUser user) {
        String sql = "UPDATE app_user SET first_name = ?, last_name = ?, date_of_birth = ?, email = ?, country_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(), user.getDateOfBirth(), user.getEmail(), user.getCountry().getId());
    }
}
