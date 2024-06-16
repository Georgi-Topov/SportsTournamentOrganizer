package bg.fmi.sports.tournament.organizer.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd.MM.yyyy")
        private LocalDateTime timestamp;
        private String error;
        private String message;
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(),
                e.getClass().getSimpleName(), "Duplicate of data"), HttpStatus.BAD_REQUEST);
        // TODO: more accurate message about error
    }

    @ExceptionHandler({InvalidRoleException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRoleException(InvalidRoleException e) {
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), e.getClass().getSimpleName(),
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(), e.getClass().getSimpleName(),
                e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
