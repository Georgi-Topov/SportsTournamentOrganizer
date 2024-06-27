package bg.fmi.sports.tournament.organizer.exception;

public class TeamToTournamentBadCorrespondenceException extends BadCorrespondenceException {

    public TeamToTournamentBadCorrespondenceException(String message) {
        super(message);
    }

    public TeamToTournamentBadCorrespondenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
