package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @Transactional
    void deleteByEmail(String email);
    AppUser findAppUserByEmail(String email);
}
