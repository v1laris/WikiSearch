package com.wks.wikisearch.service;

import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.AppUserCustomRepository;
import com.wks.wikisearch.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.wks.wikisearch.repository.CountryRepository;

@Service
@AllArgsConstructor
@Primary
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;
    private final CountryRepository countryRepository;
    private final AppUserCustomRepository appUserCustomRepository;
    @Override
    public List<AppUser> findAllUsers() {
        return repository.findAll();
    }


    public AppUser saveUserWithCountry(AppUser user, String countryName) {
        Country country = countryRepository.findCountryByName(countryName);
        user.setCountry(country);
        return repository.save(user);
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
        AppUser updatedUser = findByEmail(user.getEmail());
        if(updatedUser != null){
            appUserCustomRepository.updateUser(user);
        }
        return "User updated.";
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        repository.deleteByEmail(email);
    }
}
