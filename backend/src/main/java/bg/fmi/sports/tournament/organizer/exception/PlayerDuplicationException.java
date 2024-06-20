package bg.fmi.sports.tournament.organizer.exception;

public class PlayerDuplicationException extends RuntimeException {

    public PlayerDuplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerDuplicationException(String message) {
        super(message);
    }

}
