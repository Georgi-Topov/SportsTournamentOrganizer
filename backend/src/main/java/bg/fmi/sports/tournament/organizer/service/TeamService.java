package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.PlayerNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final SportTypeRepository sportTypeRepository;
    private final PlayerRepository playerRepository;
    private final MembershipService membershipService;
    private final ParticipationService participationService;

    public TeamService(TeamRepository teamRepository, SportTypeRepository sportTypeRepository,
                       PlayerRepository playerRepository, MembershipService membershipService,
                       ParticipationService participationService) {
        this.teamRepository = teamRepository;
        this.sportTypeRepository = sportTypeRepository;
        this.playerRepository = playerRepository;
        this.membershipService = membershipService;
        this.participationService = participationService;
    }

    public Team createTeam(Team team) {
        setSportTypeWithoutDuplicationForCreation(team);
        return teamRepository.save(team);
    }

    public Page<Team> findAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    public Optional<Team> findTeamById(Long id) {
        return teamRepository.findById(id);
    }

    @Transactional
    public Team partiallyUpdateTeamById(Long id, Team team) {
        team.setId(id);

        return teamRepository.findById(id).map(fetchedTeam -> {
            Optional.ofNullable(team.getName()).ifPresent(fetchedTeam::setName);

            setSportTypeWithoutDuplicationForModification(team);
            Optional.ofNullable(team.getSportType()).ifPresent(fetchedTeam::setSportType);

            // todo(maybe) : make it possible to change owning manager
            return teamRepository.save(fetchedTeam);
        }).orElseThrow(() -> new TeamNotFoundException("Team can not be found in the database"));
    }

    public void deleteTeamById(Long id) {
        if (membershipService.isTeamRegistered(id)) {
            throw new PlayerAlreadyInTeamException("There are players in the team");
        }

        if (participationService.isTeamRegistered(id)) {
            throw new TeamAlreadyInTournamentException("The team is registered to a tournament");
        }

        teamRepository.findById(id).ifPresent(team -> team.setSportType(null));
        teamRepository.deleteById(id);
    }

    private void setSportTypeWithoutDuplicationForCreation(Team team) {
        SportType teamSportType = null;

        if (team.getSportType() != null) {
            teamSportType = sportTypeRepository.findBySportType(team.getSportType().getSportType());
        }

        if (teamSportType != null) {
            team.setSportType(teamSportType);
        }
    }

    private void setSportTypeWithoutDuplicationForModification(Team team) {
        SportType teamSportType = null;

        if (team.getSportType() != null) {
            teamSportType = sportTypeRepository.findBySportType(team.getSportType().getSportType());
        }

        team.setSportType(teamSportType);
    }

    public void registerPlayer(Long teamId, Long playerId) {
        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) {
            throw new TeamNotFoundException("Team with an id of " + teamId + " is not present in the database");
        }

        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isEmpty()) {
            throw new PlayerNotFoundException("Player with an id of " + playerId + " is not present in the database");
        }

        Team fetchedTeam = team.get();
        membershipService.addPlayerToTeam(fetchedTeam, player.get());
    }

}
