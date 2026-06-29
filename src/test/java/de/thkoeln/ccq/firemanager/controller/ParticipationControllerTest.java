package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.service.ParticipationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationControllerTest {

    @Mock
    private ParticipationService participationService;

    @InjectMocks
    private ParticipationController participationController;

    private List<String> userIds;

    @BeforeEach
    void setUp() {
        userIds = Arrays.asList("user1", "user2");
    }

    @Test
    void addParticipants() {
        doNothing().when(participationService).addParticipants(anyString(), any(List.class));

        ResponseEntity<Void> response = participationController.addParticipants("app1", userIds);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(participationService, times(1)).addParticipants("app1", userIds);
    }

    @Test
    void removeParticipants() {
        doNothing().when(participationService).removeParticipants(anyString(), any(List.class));

        ResponseEntity<Void> response = participationController.removeParticipants("app1", userIds);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(participationService, times(1)).removeParticipants("app1", userIds);
    }

    @Test
    void acceptParticipation() {
        doNothing().when(participationService).acceptParticipation(anyString(), anyString());

        ResponseEntity<Void> response = participationController.acceptParticipation("app1", "user1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(participationService, times(1)).acceptParticipation("app1", "user1");
    }

    @Test
    void declineParticipation() {
        doNothing().when(participationService).declineParticipation(anyString(), anyString());

        ResponseEntity<Void> response = participationController.declineParticipation("app1", "user1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(participationService, times(1)).declineParticipation("app1", "user1");
    }

    @Test
    void getParticipationStatus() {
        when(participationService.getParticipationStatus(anyString(), anyString())).thenReturn("ACCEPTED");

        ResponseEntity<String> response = participationController.getParticipationStatus("app1", "user1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ACCEPTED", response.getBody());
        verify(participationService, times(1)).getParticipationStatus("app1", "user1");
    }
}
