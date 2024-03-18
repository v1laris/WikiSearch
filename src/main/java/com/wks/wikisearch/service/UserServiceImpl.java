package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.UserCustomRepository;
import com.wks.wikisearch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.wks.wikisearch.repository.CountryRepository;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl {

    private final UserRepository repository;
    private final CountryRepository countryRepository;
    private static final String COUNTRY_PRIMARY_KEY = "countries";
    private static final String USER_PRIMARY_KEY = "users";

    public List<UserDTOWithCountry> findAllUsers() {
        if (CacheManager.containsKey(USER_PRIMARY_KEY)) {
            return (List<UserDTOWithCountry>)CacheManager.get(USER_PRIMARY_KEY);

        } else {
            List<User> users = repository.findAll();
            List<UserDTOWithCountry> appUserDTOs = new ArrayList<>();
            for (User user : users) {
                UserDTOWithCountry appUserDTO = Conversion.convertAppUserWithCountry(user);
                appUserDTOs.add(appUserDTO);
            }
            for(UserDTOWithCountry user : appUserDTOs){
                CacheManager.put(USER_PRIMARY_KEY + "/" + user.getEmail(), user);
            }
            CacheManager.put(USER_PRIMARY_KEY, appUserDTOs);
            return appUserDTOs;
        }
    }

    public List<UserDTOWithCountry> findUsersByDateOfBirth(Integer startYear, Integer endYear) {
        LocalDate startDate = LocalDate.of(startYear, 1, 1);
        LocalDate endDate = LocalDate.of(endYear, 12, 31);
        List<User> users = repository.findUsersByDateOfBirthBetween(startDate, endDate);
        List<UserDTOWithCountry> appUserDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTOWithCountry appUserDTO = Conversion.convertAppUserWithCountry(user);
            appUserDTOs.add(appUserDTO);
        }
        return appUserDTOs;
    }

    public void saveUserWithCountry(User user, String countryName) {
        if(repository.existsByEmail(user.getEmail())){
            return;
        }
        if(countryRepository.existsByName(countryName)){
            Country country = countryRepository.findCountryByName(countryName);
            user.setCountry(country);
            repository.save(user);
            if(CacheManager.containsKey(USER_PRIMARY_KEY)){
                List<UserDTOWithCountry> users = (List<UserDTOWithCountry>)CacheManager.get(USER_PRIMARY_KEY);
                users.add(Conversion.convertAppUserWithCountry(user));
                CacheManager.put(USER_PRIMARY_KEY, users);
            }
            if(CacheManager.containsKey(COUNTRY_PRIMARY_KEY + "/" + countryName)){
                CacheManager.remove(COUNTRY_PRIMARY_KEY + "/" + countryName);
                CacheManager.remove(COUNTRY_PRIMARY_KEY);
            }
        }
    }


    public UserDTOWithCountry findByEmail(String email) {
        String cacheKey = USER_PRIMARY_KEY + "/" + email;
        UserDTOWithCountry cachedValue = (UserDTOWithCountry) CacheManager.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        } else {
            if(CacheManager.containsKey(USER_PRIMARY_KEY)){
                List<UserDTOWithCountry> users = (List<UserDTOWithCountry>)CacheManager.get(USER_PRIMARY_KEY);
                Optional<UserDTOWithCountry> findResult = users.stream()
                        .filter(userDTOWithCountry -> userDTOWithCountry
                                .getEmail().equals(email)).findFirst();
                if(findResult.isPresent()) {
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
            }
            if (repository.existsByEmail(email)) {
                UserDTOWithCountry result = Conversion.convertAppUserWithCountry(repository.findUserByEmail(email));
                CacheManager.put(cacheKey, result);
                return result;
            }
            return null;
        }
    }

    public void updateUser(User user) {
        Optional<User> temp = repository.findById(user.getId());
        if(temp.isPresent()) {
            User userToUpdate = temp.get();
            if(!Objects.equals(userToUpdate.getEmail(), user.getEmail())){
                CacheManager.remove(USER_PRIMARY_KEY + "/" + userToUpdate.getEmail());
            }
            userToUpdate.setEmail(user.getEmail());
            if(userToUpdate.getCountry() != user.getCountry()){
                CacheManager.remove(COUNTRY_PRIMARY_KEY);
                CacheManager.remove(COUNTRY_PRIMARY_KEY + "/" + userToUpdate.getCountry().getName());
            }
            CacheManager.remove(COUNTRY_PRIMARY_KEY + "/" + user.getCountry().getName());
            userToUpdate.setCountry(user.getCountry());
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setDateOfBirth(user.getDateOfBirth());

            if (CacheManager.containsKey(USER_PRIMARY_KEY)) {
                List<UserDTOWithCountry> users = (List<UserDTOWithCountry>) CacheManager.get(USER_PRIMARY_KEY);
                users.removeIf(userDTOWithCountry -> userDTOWithCountry.getEmail().equals(user.getEmail()));
                users.add(Conversion.convertAppUserWithCountry(userToUpdate));
                CacheManager.put(USER_PRIMARY_KEY, users);
            }
            CacheManager.put(userToUpdate.getEmail(), Conversion.convertAppUserWithCountry(userToUpdate));

            repository.save(userToUpdate);
        }
    }

    public void deleteUser(String email) {
        String cacheKey = USER_PRIMARY_KEY + "/" + email;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey(USER_PRIMARY_KEY)){
            List<UserDTOWithCountry> users = (List<UserDTOWithCountry>)CacheManager.get(USER_PRIMARY_KEY);
            users.removeIf(userDTOWithCountry -> userDTOWithCountry.getEmail().equals(email));
            CacheManager.put(USER_PRIMARY_KEY, users);
        }
        CacheManager.remove(COUNTRY_PRIMARY_KEY);
        CacheManager.remove(COUNTRY_PRIMARY_KEY + "/" + repository.findUserByEmail(email).getCountry().getName());
        repository.deleteByEmail(email);
    }
}
