package bg.fmi.sports.tournament.organizer.exception;

public abstract class BadCorrespondenceException extends RuntimeException {

    public BadCorrespondenceException(String message) {
        super(message);
    }

    public BadCorrespondenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
