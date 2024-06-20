package bg.fmi.sports.tournament.organizer.exception;

public class InvalidStartEndDateForTournamentException extends RuntimeException {

    public InvalidStartEndDateForTournamentException(String message) {
        super(message);
    }

    public InvalidStartEndDateForTournamentException(String message, Throwable cause) {
        super(message, cause);
    }

}
