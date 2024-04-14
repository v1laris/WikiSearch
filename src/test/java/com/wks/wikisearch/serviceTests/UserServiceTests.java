package com.wks.wikisearch.serviceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.wks.wikisearch.dto.UserDTOWithCountry;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.repository.UserCustomRepository;
import com.wks.wikisearch.repository.UserRepository;
import com.wks.wikisearch.repository.CountryRepository;
import com.wks.wikisearch.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;


    @Mock
    private UserCustomRepository userCustomRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFindAllUsers() {
        // Создаем список пользователей, которые будут возвращаться из userRepository.findAll()
        List<User> users = new ArrayList<>();
        // Добавляем пользователей в список (можете добавить столько пользователей, сколько нужно для вашего теста)
        User user1 = new User(/* Здесь передайте нужные аргументы для конструктора User */);
        Country country = new Country();
        country.setId(1L);
        countryRepository.save(country);
        user1.setCountry(country);
        user1.setDateOfBirth(LocalDate.now());
        users.add(user1);
        // Подготавливаем заглушку для userRepository.findAll(), чтобы он возвращал наш список пользователей
        when(userRepository.findAll()).thenReturn(users);
        List<UserDTOWithCountry> result = userService.findAllUsers();
        assertEquals(1, result.size()); // Проверяем размер списка
    }

    @Test
    void testFindAllUsers_NoUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        List<UserDTOWithCountry> result = userService.findAllUsers();
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveUserWithCountry_NewUserAndCountryExists() {
        User user = new User();
        user.setEmail("test@example.com");
        when(countryRepository.existsByName(anyString())).thenReturn(true);
        userService.saveUserWithCountry(user, "CountryName");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testSaveUserWithCountry_UserAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(ObjectAlreadyExistsException.class, () -> userService.saveUserWithCountry(user, "CountryName"));
    }

    @Test
    void testFindByEmail_UserExists() {
        User user = new User();
        Country country = new Country();
        country.setId(1L);
        countryRepository.save(country);
        user.setEmail("test@example.com");
        user.setCountry(country);
        user.setDateOfBirth(LocalDate.now());
        userRepository.save(user);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
        UserDTOWithCountry result = userService.findByEmail("test@example.com");
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        assertThrows(ObjectNotFoundException.class, () -> userService.findByEmail("test@example.com"));
    }

    @Test
    void testUpdateUser_UserExists() {
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setEmail("test@example.com");
        User user = new User();
        user.setId(1L);
        user.setEmail("new_email@example.com");
        Country country = new Country();
        country.setName("CountryName");
        user.setCountry(country);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.existsByEmail("new_email@example.com")).thenReturn(false);
        userService.updateUser(user);
        verify(userCustomRepository, times(1)).updateUser(userToUpdate);
    }

    @Test
    void testUpdateUser_UserDoesNotExist() {
        User user = new User();
        user.setId(1L);
        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        // Arrange
        String existingEmail = "existing@example.com";
        String newUserEmail = "new@example.com";

        // Создаем имитацию пользователя с новым email
        User newUser = new User();
        newUser.setEmail(newUserEmail);

        // Создаем имитацию пользователя с существующим email
        User existingUser = new User();
        existingUser.setEmail(existingEmail);

        // Подделываем ответы от репозитория
        when(userRepository.findById(any())).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail(newUserEmail)).thenReturn(true);

        // Act & Assert
        assertThrows(ObjectAlreadyExistsException.class, () -> {
            userService.updateUser(newUser);
        });
    }

    @Test
    void testDeleteUser_UserExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        userService.deleteUser("test@example.com");
        verify(userRepository, times(1)).deleteByEmail("test@example.com");
    }

    @Test
    void testDeleteUser_UserDoesNotExist() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser("test@example.com"));
    }
}

