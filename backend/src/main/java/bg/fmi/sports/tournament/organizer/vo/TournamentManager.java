package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.entity.Match;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.exception.NotEnoughTeamsInTournament;
import bg.fmi.sports.tournament.organizer.service.TournamentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class TournamentManager {
    private static final int TIME_INTERVAL_MINUTES = 1;

    private final Set<Tournament> tournaments;

    private final Map<Long, Set<Team>> remainingTeams;

    private final TournamentService tournamentService;

    public TournamentManager(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
        tournaments = new HashSet<>();
        remainingTeams = new HashMap<>();
    }

    public void startTournament(Tournament tournament) throws NotEnoughTeamsInTournament {
        Set<Team> teams = tournamentService.getAllTeams(tournament.getId());
        if (teams.size() < tournament.getSportType().getCountOfTeamInMatch()) {
            throw new NotEnoughTeamsInTournament("There not enough teams for the tournament to start: "
                    + remainingTeams.size());
        }
        tournaments.add(tournament);
        remainingTeams.put(tournament.getId(), teams);
        tournament.setStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @Scheduled(fixedDelay = TIME_INTERVAL_MINUTES, timeUnit = TimeUnit.MINUTES)
    public void checkMatches() {

    }

}
