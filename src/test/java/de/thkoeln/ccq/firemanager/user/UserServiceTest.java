package de.thkoeln.ccq.firemanager.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser() {
        User user = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals(user, createdUser);
    }

    @Test
    void getAllUsers() {
        User user1 = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        User user2 = new User("Anna", "Schmidt", LocalDate.of(1995, 5, 5), LocalDate.of(2021, 5, 5), "Schmidtstraße 2");
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertEquals(users, allUsers);
    }

    @Test
    void getUserById() {
        User user = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    @Test
    void updateUser() {
        User existingUser = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Musterstraße 1");
        User updatedUserDetails = new User("Max", "Mustermann", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1), "Neue Straße 2");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUserDetails);

        User updatedUser = userService.updateUser(1L, updatedUserDetails);

        assertEquals(updatedUserDetails.getAdresse(), updatedUser.getAdresse());
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }
}
