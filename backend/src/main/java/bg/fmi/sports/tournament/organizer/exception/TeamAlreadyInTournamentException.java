package bg.fmi.sports.tournament.organizer.exception;

public class TeamAlreadyInTournamentException extends EntitiesRelationshipAlreadyExistException {

    public TeamAlreadyInTournamentException(String message) {
        super(message);
    }

    public TeamAlreadyInTournamentException(String message, Throwable cause) {
        super(message, cause);
    }

}
