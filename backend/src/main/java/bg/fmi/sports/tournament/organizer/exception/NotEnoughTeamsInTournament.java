package bg.fmi.sports.tournament.organizer.exception;

public class NotEnoughTeamsInTournament extends Exception {

    public NotEnoughTeamsInTournament(String message) {
        super(message);
    }

    public NotEnoughTeamsInTournament(String message, Throwable cause) {
        super(message, cause);
    }
}
