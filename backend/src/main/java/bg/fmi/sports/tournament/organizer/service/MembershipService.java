package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.PlayerNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public void addPlayerToTeam(Team team, Player player) {
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

        membershipRepository.save(membership);
    }

    public Page<Membership> findAllMemberships(Pageable pageable) {
        return membershipRepository.findAll(pageable);
    }

    public void assignPlayerToTeam(Long teamId, Long playerId) {
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

        addPlayerToTeam(fetchedTeam, fetchedPlayer);
    }

    public void checkPlayerMembershipStatus(Long playerId) {
        Optional<Long> latestTeamId = membershipRepository.findLatestTeamForPlayer(playerId);

        if (latestTeamId.isPresent()) {
            try {
                checkTeamParticipationStatus(latestTeamId.get());
            } catch (TeamAlreadyInTournamentException ex) {
                throw new TeamAlreadyInTournamentException(
                    "The player is currently registered to a team with an id of "
                        + latestTeamId.get() + " and " + ex.getMessage(), ex);
            }
        } else {
            throw new TeamNotFoundException("The team was not found");
        }
    }

}
