package de.thkoeln.ccq.firemanager.calendar.interfaces;

import de.thkoeln.ccq.firemanager.calendar.application.CalendarNotFoundException;
import de.thkoeln.ccq.firemanager.calendar.application.EventNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "de.thkoeln.ccq.firemanager.calendar")
public class GlobalExceptionHandler {
    
    @ExceptionHandler(CalendarNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCalendarNotFound(CalendarNotFoundException ex) {
        var error = new ErrorResponse(
                "CALENDAR_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(EventNotFoundException ex) {
        var error = new ErrorResponse(
                "EVENT_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    public record ErrorResponse(
            String errorCode,
            String message,
            LocalDateTime timestamp,
            int status
    ) {}
}