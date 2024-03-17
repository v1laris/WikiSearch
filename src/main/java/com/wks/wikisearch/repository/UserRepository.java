package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    void deleteByEmail(String email);
    User findUserByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE date_of_birth BETWEEN ?1 AND ?2", nativeQuery = true)
    List<User> findUsersByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);
}
