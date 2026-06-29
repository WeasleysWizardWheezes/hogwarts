package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.ExerciseDTO;
import de.thkoeln.ccq.firemanager.model.Exercise;
import de.thkoeln.ccq.firemanager.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseDTO createExercise(ExerciseDTO exerciseDTO) {
        Exercise exercise = mapToEntity(exerciseDTO);
        Exercise savedExercise = exerciseRepository.save(exercise);
        return mapToDTO(savedExercise);
    }

    public ExerciseDTO getExerciseById(String id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        return mapToDTO(exercise);
    }

    public List<ExerciseDTO> getAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ExerciseDTO> getExercisesByOrganizationUnit(String organizationUnitId) {
        return exerciseRepository.findByOrganizationUnitId(organizationUnitId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ExerciseDTO updateExercise(String id, ExerciseDTO exerciseDTO) {
        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        
        existingExercise.setTitle(exerciseDTO.getTitle());
        existingExercise.setDescription(exerciseDTO.getDescription());
        existingExercise.setStartDate(exerciseDTO.getStartDate());
        existingExercise.setEndDate(exerciseDTO.getEndDate());
        existingExercise.setRecurring(exerciseDTO.isRecurring());
        existingExercise.setRecurrencePattern(exerciseDTO.getRecurrencePattern());
        
        Exercise updatedExercise = exerciseRepository.save(existingExercise);
        return mapToDTO(updatedExercise);
    }

    public void deleteExercise(String id) {
        exerciseRepository.deleteById(id);
    }

    private Exercise mapToEntity(ExerciseDTO exerciseDTO) {
        return Exercise.builder()
                .id(exerciseDTO.getId())
                .title(exerciseDTO.getTitle())
                .description(exerciseDTO.getDescription())
                .startDate(exerciseDTO.getStartDate())
                .endDate(exerciseDTO.getEndDate())
                .isRecurring(exerciseDTO.isRecurring())
                .recurrencePattern(exerciseDTO.getRecurrencePattern())
                .build();
    }

    private ExerciseDTO mapToDTO(Exercise exercise) {
        return ExerciseDTO.builder()
                .id(exercise.getId())
                .title(exercise.getTitle())
                .description(exercise.getDescription())
                .startDate(exercise.getStartDate())
                .endDate(exercise.getEndDate())
                .isRecurring(exercise.isRecurring())
                .recurrencePattern(exercise.getRecurrencePattern())
                .organizationUnitId(exercise.getOrganizationUnit() != null ? exercise.getOrganizationUnit().getId() : null)
                .build();
    }
}
