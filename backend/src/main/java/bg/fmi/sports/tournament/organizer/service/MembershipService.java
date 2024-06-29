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
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("membership")
public class MembershipService extends AffiliationService {

    private final MembershipRepository membershipRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public MembershipService(TournamentRepository tournamentRepository,
                             MembershipRepository membershipRepository, ParticipationRepository participationRepository,
                             PlayerRepository playerRepository, TeamRepository teamRepository) {
        super(participationRepository, tournamentRepository);
        this.membershipRepository = membershipRepository;
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    private Membership addPlayerToTeam(Team team, Player player) {
        MembershipId membershipId = new MembershipId(team.getId(), player.getId());

        Membership membership = Membership.builder()
            .id(membershipId)
            .team(team)
            .player(player)
            .audit(new Audit())
            .build();

        if (membershipRepository.existsById(new MembershipId(team.getId(), player.getId()))) {
            throw new PlayerAlreadyInTeamException("The player is already assigned to the team");
        }

        return membershipRepository.save(membership);
    }

    public Membership assignPlayerToTeam(Long teamId, Long playerId) {
        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) {
            throw new TeamNotFoundException("Team with an id of " + teamId + " is not present in the database");
        }

        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) {
            throw new PlayerNotFoundException("Player with an id of " + playerId + " is not present in the database");
        }

        Team fetchedTeam = team.get();
        Player fetchedPlayer = player.get();

        checkTeamParticipationStatus(teamId);
        checkPlayerMembershipStatus(playerId);

        return addPlayerToTeam(fetchedTeam, fetchedPlayer);
    }

    private void checkPlayerMembershipStatus(Long playerId) {
        Optional<Long> latestTeamId = membershipRepository.findLatestTeamForPlayer(playerId);

        if (latestTeamId.isPresent()) {
            try {
                checkLatestTeamFinishedParticipation(latestTeamId.get());
            } catch (TeamAlreadyInTournamentException ex) {
                throw new PlayerAlreadyInTeamException(
                    "The player is currently registered to a team with an id of "
                        + latestTeamId.get() + " and " + ex.getMessage(), ex);
            }
        }
    }

    private void checkLatestTeamFinishedParticipation(Long teamId) {
        Optional<Tournament> latestTournament;
        Optional<Long> latestTournamentId = participationRepository.findLatestTournamentForTeam(teamId);

        if (latestTournamentId.isPresent()) {
            latestTournament = tournamentRepository.findById(latestTournamentId.get());

            if (latestTournament.isPresent()) {
                if (LocalDateTime.now().isBefore(latestTournament.get().getEndDate())) {
                    throw new TeamAlreadyInTournamentException(
                        "The team is currently registered to a tournament with an id of "
                            + latestTournament.get().getId().toString() + " which is not over yet");
                } else {
                    return;
                }
            } else {
                throw new TournamentNotFoundException("The tournament was not found");
            }

        }

        throw new PlayerAlreadyInTeamException("The player is already assigned to a team with an id of "
            + teamId + " which will be registered in a tournament");
    }

    public Page<Membership> findAllMemberships(Pageable pageable) {
        return membershipRepository.findAll(pageable);
    }

}
