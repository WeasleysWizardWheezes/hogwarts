package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        User user = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserById() {
        String userId = "123";
        User user = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserByIdNotFound() {
        String userId = "123";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(userId);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getAllUsers() {
        User user1 = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        User user2 = new User("Anna", "Musterfrau", LocalDate.of(1995, 5, 5), LocalDate.of(2021, 5, 5), "Musterstraße 2");
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(users, response.getBody());
    }
}
