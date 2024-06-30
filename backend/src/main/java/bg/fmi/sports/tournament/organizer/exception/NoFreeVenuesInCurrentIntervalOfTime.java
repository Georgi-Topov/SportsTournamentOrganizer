package bg.fmi.sports.tournament.organizer.exception;

public class NoFreeVenuesInCurrentIntervalOfTime extends Exception {

    public NoFreeVenuesInCurrentIntervalOfTime(String message) {
        super(message);
    }

    public NoFreeVenuesInCurrentIntervalOfTime(String message, Throwable cause) {
        super(message, cause);
    }
}
