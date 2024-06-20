package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.exception.InvalidStartEndDateForTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentOverException;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final SportTypeRepository sportTypeRepository;

    public TournamentService(TournamentRepository tournamentRepository, SportTypeRepository sportTypeRepository) {
        this.tournamentRepository = tournamentRepository;
        this.sportTypeRepository = sportTypeRepository;
    }

    public Tournament createTournament(Tournament tournament) {
        setSportTypeWithoutDuplication(tournament);
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

            checkTournamentDates(tournament, fetchedTournament);
            Optional.ofNullable(tournament.getStartDate()).ifPresent(fetchedTournament::setStartDate);
            Optional.ofNullable(tournament.getEndDate()).ifPresent(fetchedTournament::setEndDate);

            Optional.ofNullable(tournament.getDescription()).ifPresent(fetchedTournament::setDescription);
            Optional.ofNullable(tournament.getMinimumPlayersPerTeam())
                .ifPresent(fetchedTournament::setMinimumPlayersPerTeam);
            Optional.ofNullable(tournament.getMaximumPlayersPerTeam())
                .ifPresent(fetchedTournament::setMaximumPlayersPerTeam);

            return tournamentRepository.save(fetchedTournament);
        }).orElseThrow(() -> new TournamentNotFoundException("Tournament can not be found in the database"));
    }

    private void setSportTypeWithoutDuplication(Tournament tournament) {
        SportType tournamentSportType = null;

        if (tournament.getSportType() != null && tournament.getSportType().getSportType() != null) {
            tournamentSportType = sportTypeRepository.findBySportType(tournament.getSportType().getSportType());
        }

        if (tournamentSportType != null) {
            tournament.setSportType(tournamentSportType);
        }
    }

    public void checkTournamentDates(Tournament newTournament, Tournament oldTournament) {
        if (newTournament.getStartDate() != null && newTournament.getEndDate() != null) {
            if (!newTournament.getStartDate().isBefore(newTournament.getEndDate())) {
                throw new InvalidStartEndDateForTournamentException("The start date cannot be after the end date");
            }

            if (oldTournament.getEndDate().isBefore(LocalDateTime.now())) {
                throw new TournamentOverException("Cannot change anything for a tournament when it is over");
            }

            if (newTournament.getStartDate().isBefore(LocalDateTime.now())) {
                throw new InvalidStartEndDateForTournamentException(
                    "The start date cannot be changed to an earlier date than today"
                );
            }

            if (!oldTournament.getStartDate().isAfter(LocalDateTime.now())
                && !oldTournament.getEndDate().isBefore(LocalDateTime.now())) {
                newTournament.setStartDate(null);
                if (newTournament.getEndDate().isBefore(LocalDateTime.now())) {
                    throw new InvalidStartEndDateForTournamentException(
                        "The end date cannot be changed to an earlier date than today"
                    );
                }
            }
        }
    }

}
