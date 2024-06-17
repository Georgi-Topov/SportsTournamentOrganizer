package bg.fmi.sports.tournament.organizer.exception;


public class PlayerNotFoundException extends EntityNotFoundException {

    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
