package com.wks.wikisearch.servise.impl;

import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.repository.AppUserRepository;
import com.wks.wikisearch.servise.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Primary
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;
    @Override
    public List<AppUser> findAllUsers() {
        return repository.findAll();
    }

    @Override
    public String saveUser(AppUser user) {
        repository.save(user);
        return "User saved.";
    }

    @Override
    public AppUser findByEmail(String email) {
        return repository.findAppUserByEmail(email);
    }

    @Override
    public String updateUser(AppUser user) {
        repository.save(user);
        return "User updated.";
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        repository.deleteByEmail(email);
    }
}
