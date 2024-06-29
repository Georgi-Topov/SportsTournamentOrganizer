package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.entity.Match;
import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.repository.MatchRepository;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.SequencedCollection;
import java.util.Set;

public class MatchCreator {
    private SequencedCollection<Team> teams;

    private final Set<Match> activeMatches;

    private final MatchRepository matchRepository;

    private final SportType type;

    private boolean canCreateMatch() {
        return teams.size() % type.getCountOfTeamInMatch() == 0 && !teams.isEmpty();
    }

    public MatchCreator(SportType type, MatchRepository matchRepository) {
        this.type = type;
        teams = new ArrayDeque<>();
        activeMatches = new HashSet<>();
        this.matchRepository = matchRepository;
    }

    public void addTeam(Team team) {
        teams.add(team);
        if (canCreateMatch()) {
            for (int i = 0; i < type.getCountOfTeamInMatch(); i++) {

            }
        }
    }
}
