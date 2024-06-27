package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.PlayerNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service("membership")
public class MembershipService extends AffiliationService {

    private final MembershipRepository membershipRepository;
    private final PlayerRepository playerRepository;

    public MembershipService(UserService userService, TournamentRepository tournamentRepository,
                             MembershipRepository membershipRepository, ParticipationRepository participationRepository,
                             PlayerRepository playerRepository, TeamRepository teamRepository) {
        super(userService, participationRepository, tournamentRepository, teamRepository);
        this.membershipRepository = membershipRepository;
        this.playerRepository = playerRepository;
    }

    private Membership addPlayerToTeam(Team team, Player player) {
        MembershipId membershipId = new MembershipId(team.getId(), player.getId(), Audit.builder().build());

        Membership membership = Membership.builder()
            .id(membershipId)
            .team(team)
            .player(player)
            .build();

        return membershipRepository.save(membership);
    }

    public Membership assignPlayerToTeam(Long teamId, Long playerId, HttpServletRequest request) {
        Team fetchedTeam = checkTeamPresenceInTheDatabase(teamId);
        Player fetchedPlayer = checkPlayerPresenceInTheDatabase(playerId);

        validateTeamOwnership(fetchedTeam, request);

        checkTeamParticipationStatus(teamId);
        checkPlayerMembershipStatus(playerId);

        return addPlayerToTeam(fetchedTeam, fetchedPlayer);
    }

    private Player checkPlayerPresenceInTheDatabase(Long playerId) {
        Optional<Player> player = playerRepository.findById(playerId);

        if (player.isEmpty()) {
            throw new PlayerNotFoundException("There is no player with an id " + playerId);
        } else {
            return player.get();
        }
    }

    private void checkPlayerMembershipStatus(Long playerId) {
        Optional<Long> latestTeamId = membershipRepository.findLatestTeamForPlayer(playerId);

        if (latestTeamId.isPresent()) {
            try {
                checkLatestTeamFinishedParticipation(playerId, latestTeamId.get());
            } catch (TeamAlreadyInTournamentException ex) {
                throw new PlayerAlreadyInTeamException(
                    "The player is currently assigned to a team with an id "
                        + latestTeamId.get() + " and " + ex.getMessage(), ex);
            }
        }
    }

    private void checkLatestTeamFinishedParticipation(Long playerId, Long teamId) {
        Optional<Tournament> latestTournament;
        Optional<Long> latestTournamentId = participationRepository.findLatestTournamentForTeam(teamId);

        if (latestTournamentId.isPresent()) {
            latestTournament = tournamentRepository.findById(latestTournamentId.get());

            if (latestTournament.isPresent()) {
                if (LocalDateTime.now().isBefore(latestTournament.get().getEndDate())) {
                    throw new TeamAlreadyInTournamentException(
                        "The team is currently registered for a tournament with an id "
                            + latestTournament.get().getId().toString() + " which is not over yet"
                    );
                } else {
                    Optional<Long> playerInTeamAfterItsLastTournament =
                        membershipRepository.findPlayerPresenceAfterLastTeamTournament(playerId, teamId,
                        latestTournamentId.get());

                    if (playerInTeamAfterItsLastTournament.isPresent()) {
                        throw new PlayerAlreadyInTeamException("The player is already assigned to a team with an id "
                            + teamId + " which will be registered for a tournament");
                    }
                    return;
                }
            } else {
                throw new TournamentNotFoundException("The tournament was not found");
            }
        }

        throw new PlayerAlreadyInTeamException("The player is already assigned to a team with an id "
            + teamId + " which will be registered for a tournament");
    }

    public Set<Membership> findAllAssignedPlayers(Long teamId) {
        Optional<Long> latestTournamentForTeam = participationRepository.findLatestTournamentForTeam(teamId);
        checkTeamPresenceInTheDatabase(teamId);

        Set<Membership> playersInATeam;
        if (latestTournamentForTeam.isPresent()) {
            playersInATeam = membershipRepository.findPlayersInTeam(teamId, latestTournamentForTeam.get());
        } else {
            playersInATeam = membershipRepository.findPlayersInTeamWhenTeamHasNotParticipatedYet(teamId);
        }

        return playersInATeam;
    }

}
