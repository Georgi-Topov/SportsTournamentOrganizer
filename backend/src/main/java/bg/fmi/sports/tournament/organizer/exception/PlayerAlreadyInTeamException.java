package bg.fmi.sports.tournament.organizer.exception;

public class PlayerAlreadyInTeamException extends EntitiesRelationshipAlreadyExistException {

    public PlayerAlreadyInTeamException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAlreadyInTeamException(String message) {
        super(message);
    }

}
