package bg.fmi.sports.tournament.organizer.exception;

public class TournamentNotFoundException extends EntityNotFoundException {

    public TournamentNotFoundException(String message) {
        super(message);
    }

    public TournamentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
