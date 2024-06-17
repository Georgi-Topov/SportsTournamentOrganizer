package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final SportTypeRepository sportTypeRepository;
    private final TeamRepository teamRepository;
    private final ParticipationService participationService;

    public TournamentService(TournamentRepository tournamentRepository, SportTypeRepository sportTypeRepository, 
                             TeamRepository teamRepository, ParticipationService participationService) {
        this.tournamentRepository = tournamentRepository;
        this.sportTypeRepository = sportTypeRepository;
        this.teamRepository = teamRepository;
        this.participationService = participationService;
    }

    public Tournament createTournament(Tournament tournament) {
        setSportTypeWithoutDuplicationForCreation(tournament);
        return tournamentRepository.save(tournament);
    }

    public Page<Tournament> findAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    public Optional<Tournament> findTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    @Transactional
    public Tournament partiallyUpdateTournamentById(Long id, Tournament tournament) {
        tournament.setId(id);

        return tournamentRepository.findById(id).map(fetchedTournament -> {
            Optional.ofNullable(tournament.getName()).ifPresent(fetchedTournament::setName);
            Optional.ofNullable(tournament.getStartDate()).ifPresent(fetchedTournament::setStartDate);
            Optional.ofNullable(tournament.getEndDate()).ifPresent(fetchedTournament::setEndDate);
            Optional.ofNullable(tournament.getDescription()).ifPresent(fetchedTournament::setDescription);

            setSportTypeWithoutDuplicationForModification(tournament);
            Optional.ofNullable(tournament.getSportType()).ifPresent(fetchedTournament::setSportType);
            // todo : to be able to set minimum and maximum number of players
            // and to create new instance of sport iff at least one of the attributes is different
            // otherwise - reuse the sport type

            return tournamentRepository.save(fetchedTournament);
        }).orElseThrow(() -> new TournamentNotFoundException("Tournament can not be found in the database"));
    }

    public void deleteTournamentById(Long id) {
        tournamentRepository.findById(id).ifPresent(tournament -> tournament.setSportType(null));
        tournamentRepository.deleteById(id);
    }

    private void setSportTypeWithoutDuplicationForCreation(Tournament tournament) {
        SportType tournamentSportType = null;

        if (tournament.getSportType() != null && tournament.getSportType().getSportType() != null) {
            tournamentSportType = sportTypeRepository.findBySportType(tournament.getSportType().getSportType());
        }

        if (tournamentSportType != null) {
            tournament.setSportType(tournamentSportType);
        }
    }

    private void setSportTypeWithoutDuplicationForModification(Tournament tournament) {
        SportType tournamentSportType = null;

        if (tournament.getSportType() != null && tournament.getSportType().getSportType() != null) {
            tournamentSportType = sportTypeRepository.findBySportType(tournament.getSportType().getSportType());
        }

        tournament.setSportType(tournamentSportType);
    }

    public void registerTeam(Long tournamentId, Long teamId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        if (tournament.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with an id of " + tournamentId
                + " is not present in the database");
        }

        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) {
            throw new TeamNotFoundException("Team with an id of " + teamId + " is not present in the database");
        }

        Tournament fetchedTournament = tournament.get();
        participationService.addTeamToTournament(fetchedTournament, team.get());
    }

}
