package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.service.TournamentService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class TournamentStarter {

    private static final int TIME_INTERVAL_EXECUTION_MINUTES = 1;

    private static final int TIME_INTERVAL_BETWEEN_START = 1;

    private TournamentService tournamentService;

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = TIME_INTERVAL_EXECUTION_MINUTES)
    public void startTournaments() {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime end = start.plusMinutes(TIME_INTERVAL_BETWEEN_START);
        for (Tournament tournament :
                tournamentService.getTournamentsByDateInterval(start, end)) {
            System.out.println("Test");
        }
    }
}
