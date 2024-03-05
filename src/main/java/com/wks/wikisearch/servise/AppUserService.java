package com.wks.wikisearch.servise;

import java.util.List;
import com.wks.wikisearch.model.AppUser;
public interface AppUserService {
    List<AppUser> findAllUsers();
    String saveUser(AppUser user);
    AppUser findByEmail(String email);
    String updateUser(AppUser user);
    void deleteUser(String email);
}
