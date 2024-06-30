package bg.fmi.sports.tournament.organizer.exception;

public class MissingSportTypeException extends RuntimeException {

    public MissingSportTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingSportTypeException(String message) {
        super(message);
    }

}
