package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.TournamentVenues;
import bg.fmi.sports.tournament.organizer.exception.MissingVenueException;
import bg.fmi.sports.tournament.organizer.exception.NotEnoughTeamsInTournament;
import bg.fmi.sports.tournament.organizer.repository.MatchRepository;
import bg.fmi.sports.tournament.organizer.service.TournamentService;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class TournamentManager {
    private static final int TIME_INTERVAL_MINUTES = 1;

    private final Map<Tournament, MatchCreator> creators;

    @Getter
    private final TournamentService tournamentService;

    @Getter
    private final MatchRepository matchRepository;

    public TournamentManager(TournamentService tournamentService, MatchRepository matchRepository) {
        this.tournamentService = tournamentService;
        this.matchRepository = matchRepository;
        this.creators = new HashMap<>();
    }

    public void startTournament(Tournament tournament) throws NotEnoughTeamsInTournament, MissingVenueException {
        Set<Team> teams = tournamentService.getAllTeams(tournament.getId());
        if (teams.size() < tournament.getSportType().getCountOfTeamInMatch()) {
            throw new NotEnoughTeamsInTournament("There not enough teams for the tournament to start: "
                    + teams.size());
        }
        if (tournament.getVenues() == null && tournament.getVenues().isEmpty()) {
            throw new MissingVenueException("Tournament hasn't any venue!");
        }
        creators.put(tournament, new MatchCreator(this, tournament));
        tournament.setStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    public void removeTournament(Tournament tournament) {
        creators.remove(tournament);
    }

}
