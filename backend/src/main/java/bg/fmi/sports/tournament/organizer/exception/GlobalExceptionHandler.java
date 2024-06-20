package bg.fmi.sports.tournament.organizer.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ PlayerNotFoundException.class, TeamNotFoundException.class,
                        TournamentNotFoundException.class })
    public ResponseEntity<String> notFoundTuple(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<String> occurredModificationConflict() {
        // todo : come up with appropriate logic in case
        //  the modification operation for the desired record is problematic
        return null;
    }

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

}
