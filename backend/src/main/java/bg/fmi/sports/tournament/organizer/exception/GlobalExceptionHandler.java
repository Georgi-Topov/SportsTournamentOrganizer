package bg.fmi.sports.tournament.organizer.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
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

    // TODO: use ErrorResponse
    @ExceptionHandler({ PlayerNotFoundException.class, TeamNotFoundException.class,
        TournamentNotFoundException.class })
    public ResponseEntity<String> notFoundTuple(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // TODO: use ErrorResponse
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<String> occurredModificationConflict() {
        // todo : come up with appropriate logic in case
        //  the modification operation for the desired record is problematic
        return null;
    }

    // TODO: use ErrorResponse
    @ExceptionHandler({ PlayerAlreadyInTeamException.class, TeamAlreadyInTournamentException.class })
    public ResponseEntity<String> alreadyMember(EntitiesRelationshipAlreadyExistException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PlayerDuplicationException.class)
    public ResponseEntity<String> duplicatePlayer(PlayerDuplicationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ TeamToTournamentBadCorrespondenceException.class })
    public ResponseEntity<String> badCorrespondence(BadCorrespondenceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TournamentOverException.class)
    public ResponseEntity<String> tournamentOver(TournamentOverException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidStartEndDateForTournamentException.class)
    public ResponseEntity<String> startDateBeforeEndDate(InvalidStartEndDateForTournamentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingSportTypeException.class)
    public ResponseEntity<String> missingSportType(MissingSportTypeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<String> notAuthorized(UserNotAuthorizedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

}
