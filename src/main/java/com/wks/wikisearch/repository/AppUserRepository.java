package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    void deleteByEmail(String email);
    AppUser findAppUserByEmail(String email);
}
