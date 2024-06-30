package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.exception.MissingVenueException;
import bg.fmi.sports.tournament.organizer.exception.NotEnoughTeamsInTournament;
import bg.fmi.sports.tournament.organizer.service.TournamentService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Log
public class TournamentStarter {

    private static final int TIME_INTERVAL_EXECUTION_MINUTES = 1;

    private static final int TIME_INTERVAL_BETWEEN_START = 10;

    private static final long DELAY = 3600;

    private TournamentService tournamentService;

    private TournamentManager tournamentManager;

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = TIME_INTERVAL_EXECUTION_MINUTES)
    public void startTournaments() {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime end = start.plusMinutes(TIME_INTERVAL_BETWEEN_START);
        log.info("TEST");
        for (Tournament tournament :
                tournamentService.getTournamentsByDateInterval(start, end)) {
            log.info("HAS TOURNAMENT");
            try {
                tournamentManager.startTournament(tournament);
            } catch (NotEnoughTeamsInTournament | MissingVenueException ex) {
                tournament.setStartDate(LocalDateTime.now().plusSeconds(DELAY));
            }
        }
    }
}
