package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.exception.MissingSportTypeException;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.UserNotAuthorizedException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.vo.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeamService {

    private final UserService userService;
    private final TeamRepository teamRepository;
    private final SportTypeRepository sportTypeRepository;
    private final MembershipRepository membershipRepository;
    private final ParticipationRepository participationRepository;

    public TeamService(UserService userService, TeamRepository teamRepository, SportTypeRepository sportTypeRepository,
                       MembershipRepository membershipRepository, ParticipationRepository participationRepository) {
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.sportTypeRepository = sportTypeRepository;
        this.membershipRepository = membershipRepository;
        this.participationRepository = participationRepository;
    }

    public Team createTeam(Team team, HttpServletRequest request) {
        User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);
        if (currentUser.getRole() != Role.MANAGER) {
            throw new UserNotAuthorizedException("Only a user with a manager role can create a team");
        }

        setSportTypeWithoutDuplication(team);

        team.setAudit(Audit.builder().build());
        Team savedTeam = teamRepository.save(team);
        savedTeam.setAudit(Audit.builder()
            .createdDate(savedTeam.getAudit().getCreatedDate())
            .createdBy(savedTeam.getAudit().getCreatedBy())
            .lastModifiedDate(null)
            .lastModifiedBy(null)
            .build());
        return savedTeam;
    }

    public Page<Team> findAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }

    public Optional<Team> findTeamById(Long id) {
        return teamRepository.findById(id);
    }

    @Transactional
    public Team partiallyUpdateTeamById(Long id, Team team, HttpServletRequest request) {
        team.setId(id);

        return teamRepository.findById(id).map(fetchedTeam -> {
            User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);
            if (!currentUser.getId().equals(fetchedTeam.getAudit().getCreatedBy())) {
                throw new UserNotAuthorizedException("Only the owning manager can update the team");
            }

            Optional.ofNullable(team.getName()).ifPresent(fetchedTeam::setName);
            // todo(maybe) : make it possible to change owning manager
            return teamRepository.save(fetchedTeam);
        }).orElseThrow(() -> new TeamNotFoundException("There is no team with the provided id"));
    }

    public void deleteTeamById(Long id, HttpServletRequest request) {
        Team team = teamRepository.findById(id).map(fetchedTeam -> {
            User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);

            if (!currentUser.getId().equals(fetchedTeam.getAudit().getCreatedBy())) {
                throw new UserNotAuthorizedException("Only the owning manager can delete the team");
            }

            return fetchedTeam;
        }).orElseThrow(() -> new TeamNotFoundException("There is no team with the provided id"));

        if (isTeamHavingPlayers(id)) {
            throw new PlayerAlreadyInTeamException("There are players in the team");
        }

        if (isTeamRegistered(id)) {
            throw new TeamAlreadyInTournamentException("The team is registered for a tournament");
        }

        team.setSportType(null);
        teamRepository.deleteById(id);
    }

    private void setSportTypeWithoutDuplication(Team team) {
        SportType teamSportType = null;

        if (team.getSportType() != null) {
            if (team.getSportType().getSportType() == null || team.getSportType().getSportType().isBlank()) {
                throw new MissingSportTypeException("Cannot create a team without a sport type");
            }
            teamSportType = sportTypeRepository.findBySportType(team.getSportType().getSportType());
        }

        if (teamSportType != null) {
            team.setSportType(teamSportType);
        }
    }

    private boolean isTeamRegistered(Long id) {
        return !participationRepository.findByTeamId(id).isEmpty();
    }

    private boolean isTeamHavingPlayers(Long id) {
        return !membershipRepository.findByTeamId(id).isEmpty();
    }

}
