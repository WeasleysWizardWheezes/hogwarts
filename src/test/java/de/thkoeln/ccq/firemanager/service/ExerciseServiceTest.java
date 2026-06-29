package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.ExerciseDTO;
import de.thkoeln.ccq.firemanager.model.Exercise;
import de.thkoeln.ccq.firemanager.repository.ExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseService exerciseService;

    private ExerciseDTO exerciseDTO;
    private Exercise exercise;

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

        exercise = Exercise.builder()
                .id("1")
                .title("Test Exercise")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
    }

    @Test
    void createExercise() {
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        ExerciseDTO result = exerciseService.createExercise(exerciseDTO);

        assertNotNull(result);
        assertEquals(exerciseDTO.getId(), result.getId());
        assertEquals(exerciseDTO.getTitle(), result.getTitle());
        assertEquals(exerciseDTO.getStartDate(), result.getStartDate());
    }

    @Test
    void getExerciseById() {
        when(exerciseRepository.findById("1")).thenReturn(Optional.of(exercise));

        ExerciseDTO result = exerciseService.getExerciseById("1");

        assertNotNull(result);
        assertEquals(exercise.getId(), result.getId());
        assertEquals(exercise.getTitle(), result.getTitle());
    }

    @Test
    void getExerciseById_NotFound() {
        when(exerciseRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            exerciseService.getExerciseById("1");
        });
    }

    @Test
    void getAllExercises() {
        List<Exercise> exercises = Arrays.asList(exercise);
        when(exerciseRepository.findAll()).thenReturn(exercises);

        List<ExerciseDTO> result = exerciseService.getAllExercises();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(exercise.getId(), result.get(0).getId());
    }

    @Test
    void getExercisesByOrganizationUnit() {
        List<Exercise> exercises = Arrays.asList(exercise);
        when(exerciseRepository.findByOrganizationUnitId("org1")).thenReturn(exercises);

        List<ExerciseDTO> result = exerciseService.getExercisesByOrganizationUnit("org1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(exercise.getId(), result.get(0).getId());
    }

    @Test
    void updateExercise() {
        when(exerciseRepository.findById("1")).thenReturn(Optional.of(exercise));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        ExerciseDTO result = exerciseService.updateExercise("1", exerciseDTO);

        assertNotNull(result);
        assertEquals(exerciseDTO.getId(), result.getId());
        assertEquals(exerciseDTO.getTitle(), result.getTitle());
    }

    @Test
    void updateExercise_NotFound() {
        when(exerciseRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            exerciseService.updateExercise("1", exerciseDTO);
        });
    }

    @Test
    void deleteExercise() {
        doNothing().when(exerciseRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            exerciseService.deleteExercise("1");
        });

        verify(exerciseRepository, times(1)).deleteById("1");
    }
}
