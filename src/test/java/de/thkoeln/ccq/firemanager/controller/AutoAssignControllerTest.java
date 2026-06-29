package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.AutoAssignRequestDTO;
import de.thkoeln.ccq.firemanager.service.AutoAssignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoAssignControllerTest {

    @Mock
    private AutoAssignService autoAssignService;

    @InjectMocks
    private AutoAssignController autoAssignController;

    private AutoAssignRequestDTO assignRequestDTO;

    @BeforeEach
    void setUp() {
        assignRequestDTO = AutoAssignRequestDTO.builder()
                .appointmentId("app1")
                .ruleId("rule1")
                .build();
    }

    @Test
    void autoAssign() {
        doNothing().when(autoAssignService).autoAssign(any(String.class), any(String.class));

        ResponseEntity<Void> response = autoAssignController.autoAssign(assignRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
