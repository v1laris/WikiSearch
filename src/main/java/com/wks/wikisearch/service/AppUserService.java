package com.wks.wikisearch.service;

import java.util.List;

import com.wks.wikisearch.dto.AppUserDTO;
import com.wks.wikisearch.dto.AppUserDTOWithCountry;
import com.wks.wikisearch.model.AppUser;
import org.springframework.transaction.annotation.Transactional;

public interface AppUserService {
    List<AppUserDTOWithCountry> findAllUsers();
    void saveUser(AppUser user);
    AppUserDTOWithCountry findByEmail(String email);
    String updateUser(AppUser user);
    void deleteUser(String email);
}
