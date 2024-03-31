package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserCustomRepository(final JdbcTemplate dbTemplate) {
        this.jdbcTemplate = dbTemplate;
    }

    public void updateUser(final User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, "
                + "date_of_birth = ?, email = ?, country_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getFirstName(), user.getLastName(),
                user.getDateOfBirth(), user.getEmail(),
                user.getCountry().getId(), user.getId());
    }
}
