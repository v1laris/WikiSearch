package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.AppUserDTO;
import com.wks.wikisearch.dto.AppUserDTOWithCountry;
import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.AppUserCustomRepository;
import com.wks.wikisearch.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import com.wks.wikisearch.repository.CountryRepository;

@Service
@AllArgsConstructor
@Transactional
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;
    private final CountryRepository countryRepository;
    private final AppUserCustomRepository appUserCustomRepository;
    @Override
    public List<AppUserDTOWithCountry> findAllUsers() {
        List<AppUser> users = repository.findAll();
        List<AppUserDTOWithCountry> appUserDTOs = new ArrayList<>();
        for (AppUser user : users) {
            AppUserDTOWithCountry appUserDTO = Convertation.convertAppUserWithCountry(user);
            appUserDTOs.add(appUserDTO);
        }
        return appUserDTOs;
    }


    public AppUser saveUserWithCountry(AppUser user, String countryName) {
        Country country = countryRepository.findCountryByName(countryName);
        user.setCountry(country);
        return repository.save(user);
    }

    @Override
    public void saveUser(AppUser user) {
        repository.save(user);
    }

    @Override
    public AppUserDTOWithCountry findByEmail(String email) {
        return Convertation.convertAppUserWithCountry(repository.findAppUserByEmail(email));
    }

    @Override
    public String updateUser(AppUser user) {
        AppUserDTOWithCountry updatedUser = findByEmail(user.getEmail());
        if(updatedUser != null){
            appUserCustomRepository.updateUser(user);
        }
        return "User updated.";
    }

    @Override
    public void deleteUser(String email) {
        repository.deleteByEmail(email);
    }
}
