package bg.fmi.sports.tournament.organizer.exception;

public class TeamNotFoundException extends EntityNotFoundException {

    public TeamNotFoundException(String message) {
        super(message);
    }

    public TeamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
