package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.UserNotAuthorizedException;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public abstract class AffiliationService {

    final UserService userService;
    final ParticipationRepository participationRepository;
    final TournamentRepository tournamentRepository;
    final TeamRepository teamRepository;

    public AffiliationService(UserService userService, ParticipationRepository participationRepository,
                              TournamentRepository tournamentRepository, TeamRepository teamRepository) {
        this.userService = userService;
        this.participationRepository = participationRepository;
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
    }

    void validateTeamOwnership(Team team, HttpServletRequest request) {
        User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);
        if (!currentUser.getId().equals(team.getAudit().getCreatedBy())) {
            throw new UserNotAuthorizedException("Only the manager of the team can assign players to it or " +
                "register the team for a tournament");
        }
    }

    Optional<Tournament> checkTeamParticipationStatus(Long teamId) {
        Optional<Tournament> latestTournament = Optional.empty();
        Optional<Long> latestTournamentId = participationRepository.findLatestTournamentForTeam(teamId);

        if (latestTournamentId.isPresent()) {
            latestTournament = tournamentRepository.findById(latestTournamentId.get());

            if (latestTournament.isPresent()) {
                if (LocalDateTime.now().isBefore(latestTournament.get().getEndDate())) {
                    throw new TeamAlreadyInTournamentException(
                        "The team is currently registered for a tournament with an id "
                            + latestTournament.get().getId().toString() + " which is not over yet");
                }
            } else {
                throw new TournamentNotFoundException("The tournament was not found");
            }
        }
        return latestTournament;
    }

    Team checkTeamPresenceInTheDatabase(Long teamId) {
        Optional<Team> team = teamRepository.findById(teamId);

        if (team.isEmpty()) {
            throw new TeamNotFoundException("There is no team with an id " + teamId);
        } else {
            return team.get();
        }
    }

}
