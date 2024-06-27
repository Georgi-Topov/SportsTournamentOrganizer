package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
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
    private final MembershipRepository membershipRepository;
    private final ParticipationRepository participationRepository;

    public TeamService(TeamRepository teamRepository, SportTypeRepository sportTypeRepository,
                       MembershipRepository membershipRepository, ParticipationRepository participationRepository) {
        this.teamRepository = teamRepository;
        this.sportTypeRepository = sportTypeRepository;
        this.membershipRepository = membershipRepository;
        this.participationRepository = participationRepository;
    }

    public Team createTeam(Team team) {
        setSportTypeWithoutDuplication(team);
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

            // todo(maybe) : make it possible to change owning manager
            return teamRepository.save(fetchedTeam);
        }).orElseThrow(() -> new TeamNotFoundException("Team can not be found in the database"));
    }

    public void deleteTeamById(Long id) {
        if (isTeamHavingPlayers(id)) {
            throw new PlayerAlreadyInTeamException("There are players in the team");
        }

        if (isTeamRegistered(id)) {
            throw new TeamAlreadyInTournamentException("The team is registered to a tournament");
        }

        teamRepository.findById(id).ifPresent(team -> team.setSportType(null));
        teamRepository.deleteById(id);
    }

    private void setSportTypeWithoutDuplication(Team team) {
        SportType teamSportType = null;

        if (team.getSportType() != null) {
            teamSportType = sportTypeRepository.findBySportType(team.getSportType().getSportType());
        }

        if (teamSportType != null) {
            team.setSportType(teamSportType);
        }
    }

    public boolean isTeamRegistered(Long id) {
        return !participationRepository.findByTeamId(id).isEmpty();
    }

    public boolean isTeamHavingPlayers(Long id) {
        return !membershipRepository.findByTeamId(id).isEmpty();
    }

}
