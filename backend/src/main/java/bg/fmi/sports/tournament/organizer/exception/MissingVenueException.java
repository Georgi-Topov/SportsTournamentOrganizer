package bg.fmi.sports.tournament.organizer.exception;

public class MissingVenueException extends Exception {

    public MissingVenueException(String message) {
        super(message);
    }

    public MissingVenueException(String message, Throwable cause) {
        super(message, cause);
    }
}
