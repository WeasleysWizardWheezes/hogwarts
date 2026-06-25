package de.thkoeln.ccq.firemanager.user;

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
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        User user = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void getAllUsers() {
        User user1 = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        User user2 = new User("Anna", "Schmidt", LocalDate.of(1995, 5, 5), LocalDate.of(2021, 5, 5), "Schmidtstraße 2");
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(users, response.getBody());
    }

    @Test
    void getUserById() {
        User user = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void getUserByIdNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void updateUser() {
        User user = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser(1L, user);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void deleteUser() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(204, response.getStatusCode().value());
    }
}
