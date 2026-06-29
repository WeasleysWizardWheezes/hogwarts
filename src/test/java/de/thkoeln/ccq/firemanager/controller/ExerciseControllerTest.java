package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.ExerciseDTO;
import de.thkoeln.ccq.firemanager.service.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseControllerTest {

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ExerciseController exerciseController;

    private ExerciseDTO exerciseDTO;

    @BeforeEach
    void setUp() {
        exerciseDTO = ExerciseDTO.builder()
                .id("1")
                .title("Test Exercise")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .organizationUnitId("org1")
                .build();
    }

    @Test
    void createExercise() {
        when(exerciseService.createExercise(any(ExerciseDTO.class))).thenReturn(exerciseDTO);

        ResponseEntity<ExerciseDTO> response = exerciseController.createExercise(exerciseDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(exerciseDTO, response.getBody());
    }

    @Test
    void getExerciseById() {
        when(exerciseService.getExerciseById(anyString())).thenReturn(exerciseDTO);

        ResponseEntity<ExerciseDTO> response = exerciseController.getExerciseById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exerciseDTO, response.getBody());
    }

    @Test
    void getAllExercises() {
        List<ExerciseDTO> exercises = Arrays.asList(exerciseDTO);
        when(exerciseService.getAllExercises()).thenReturn(exercises);

        ResponseEntity<List<ExerciseDTO>> response = exerciseController.getAllExercises();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getExercisesByOrganizationUnit() {
        List<ExerciseDTO> exercises = Arrays.asList(exerciseDTO);
        when(exerciseService.getExercisesByOrganizationUnit(anyString())).thenReturn(exercises);

        ResponseEntity<List<ExerciseDTO>> response = exerciseController.getExercisesByOrganizationUnit("org1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateExercise() {
        when(exerciseService.updateExercise(anyString(), any(ExerciseDTO.class))).thenReturn(exerciseDTO);

        ResponseEntity<ExerciseDTO> response = exerciseController.updateExercise("1", exerciseDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exerciseDTO, response.getBody());
    }

    @Test
    void deleteExercise() {
        doNothing().when(exerciseService).deleteExercise(anyString());

        ResponseEntity<Void> response = exerciseController.deleteExercise("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
