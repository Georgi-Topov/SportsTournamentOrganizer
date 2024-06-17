package bg.fmi.sports.tournament.organizer.exception;

public abstract class EntitiesRelationshipAlreadyExistException extends RuntimeException {

    public EntitiesRelationshipAlreadyExistException(String message) {
        super(message);
    }

    public EntitiesRelationshipAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

}
