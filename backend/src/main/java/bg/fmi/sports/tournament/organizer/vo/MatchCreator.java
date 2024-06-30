package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.entity.Match;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.TournamentVenues;
import bg.fmi.sports.tournament.organizer.entity.Venue;
import bg.fmi.sports.tournament.organizer.repository.MatchRepository;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MatchCreator {
    private final ArrayDeque<Team> teams;

    private final Set<List<Match>> activeMatches;

    private final MatchRepository matchRepository;

    private final Tournament tournament;

    private final List<Venue> venues;

    private final TournamentManager manager;

    private static final long INTERVAL_MINUTES = 30;

    private static final int DURATION_MINUTES = 1;

    private boolean canCreateMatch() {
        return teams.size() % tournament.getSportType().getCountOfTeamInMatch() == 0 && !teams.isEmpty();
    }

    private boolean isLastGame() {
        return activeMatches.isEmpty() && teams.size() <= tournament.getSportType().getCountOfTeamInMatch()
                && teams.size() != 1;
    }
    
    private boolean isEnd() {
        return activeMatches.isEmpty() && teams.size() == 1;
    }

    private void endOfTournament() {
        tournament.setEndDate(LocalDateTime.now());
        manager.removeTournament(tournament);
    }

    private Map.Entry<LocalDateTime, Venue> findStartDateAndVenueForMatch() {
        Map.Entry<LocalDateTime, Venue> res = null;
        for (Venue venue : venues) {
            Optional<Match> opt = matchRepository.findTopByDateTimeEndAndVenueIdEqualId(venue.getId());
            if (opt.isEmpty()) {
                return Map.entry(LocalDateTime.now(), venue);
            } else {
                if (res == null || res.getKey().isAfter(opt.get().getDateTimeEnd())) {
                    res = Map.entry(opt.get().getDateTimeEnd().plusMinutes(INTERVAL_MINUTES), venue);
                }
            }
        }
        return res;
    }

    public MatchCreator(TournamentManager manager,
                        Tournament tournament) {
        this.teams = new ArrayDeque<>();
        this.tournament = tournament;
        activeMatches = new HashSet<>();
        this.matchRepository = manager.getMatchRepository();
        this.manager = manager;
        venues = tournament.getVenues().stream().map(TournamentVenues::getVenue).collect(Collectors.toList());
        for (Team team : manager.getTournamentService().getAllTeams(tournament.getId())) {
            addTeam(team);
        }
    }

    public void addTeam(Team team) {
        teams.add(team);
        if (canCreateMatch() || isLastGame()) {
            Map.Entry<LocalDateTime, Venue> place = findStartDateAndVenueForMatch();

            List<Match> matches = new ArrayList<>();
            int size = (isLastGame() ? teams.size() : tournament.getSportType().getCountOfTeamInMatch());
            for (int i = 0; i < size; i++) {
                Match match = Match.builder()
                        .tournament(tournament)
                        .status(MatchStatus.WAIT)
                        .dateTimeStart(place.getKey())
                        .venue(place.getValue())
                        .dateTimeEnd(place.getKey().plusMinutes(tournament.getSportType()
                                .getDurationOfMatchInMinutes()))
                        .team(teams.poll())
                        .build();
                matches.add(match);
            }
            activeMatches.add(matches);
        } else if (isEnd()) {
            endOfTournament();
        }
    }

    @Scheduled(fixedDelay = DURATION_MINUTES, timeUnit = TimeUnit.MINUTES)
    void checkMatches() {
        int randomNumber;
        ArrayList<List<Match>> deleted = new ArrayList<>();
        for (List<Match> matches : activeMatches) {
            if (LocalDateTime.now().isAfter(matches.getFirst().getDateTimeEnd())) {
                randomNumber = (int)(Math.random() * matches.size());
                for (int i = 0; i < matches.size(); i++) {
                    matches.get(i).setStatus((randomNumber == i ? MatchStatus.WIN : MatchStatus.LOST));
                }
                matchRepository.saveAll(matches);
                addTeam(matches.get(randomNumber).getTeam());
                deleted.add(matches);
            }
        }

        for (List<Match> forDelete :
                deleted) {
            activeMatches.remove(forDelete);
        }
    }
}
