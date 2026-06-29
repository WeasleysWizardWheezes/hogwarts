package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.ExerciseDTO;
import de.thkoeln.ccq.firemanager.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseDTO> createExercise(@RequestBody ExerciseDTO exerciseDTO) {
        ExerciseDTO createdExercise = exerciseService.createExercise(exerciseDTO);
        return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable String id) {
        ExerciseDTO exerciseDTO = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(exerciseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDTO>> getAllExercises() {
        List<ExerciseDTO> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/organization-unit/{organizationUnitId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByOrganizationUnit(@PathVariable String organizationUnitId) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByOrganizationUnit(organizationUnitId);
        return ResponseEntity.ok(exercises);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(@PathVariable String id, @RequestBody ExerciseDTO exerciseDTO) {
        ExerciseDTO updatedExercise = exerciseService.updateExercise(id, exerciseDTO);
        return ResponseEntity.ok(updatedExercise);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable String id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
