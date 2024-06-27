package bg.fmi.sports.tournament.organizer.exception;

public class TournamentOverException extends RuntimeException {

    public TournamentOverException(String message) {
        super(message);
    }

    public TournamentOverException(String message, Throwable cause) {
        super(message, cause);
    }

}
