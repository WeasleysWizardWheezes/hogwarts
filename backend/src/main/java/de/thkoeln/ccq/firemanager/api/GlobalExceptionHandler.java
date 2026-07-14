package de.thkoeln.ccq.firemanager.api;

import de.thkoeln.ccq.firemanager.equipment.application.EquipmentAlreadyExistsException;
import de.thkoeln.ccq.firemanager.equipment.application.EquipmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EquipmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEquipmentNotFound(EquipmentNotFoundException ex) {
        var errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EquipmentAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEquipmentAlreadyExists(EquipmentAlreadyExistsException ex) {
        var errorResponse = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
            .toList();
        
        var errorResponse = new ErrorResponse(
            "VALIDATION_FAILED",
            "The request contains invalid fields.",
            details
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        var errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred.",
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    public record ErrorResponse(String error, String message, List<ErrorDetail> details) {}
    
    public record ErrorDetail(String field, String message) {}
}