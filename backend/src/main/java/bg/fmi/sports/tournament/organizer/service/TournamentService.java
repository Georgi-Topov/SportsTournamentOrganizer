package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.exception.InvalidStartEndDateForTournamentException;
import bg.fmi.sports.tournament.organizer.exception.MissingSportTypeException;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentOverException;
import bg.fmi.sports.tournament.organizer.exception.UserNotAuthorizedException;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import bg.fmi.sports.tournament.organizer.vo.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TournamentService {

    private final UserService userService;
    private final TournamentRepository tournamentRepository;
    private final SportTypeRepository sportTypeRepository;
    private final ParticipationRepository participationRepository;

    public TournamentService(UserService userService,
                             TournamentRepository tournamentRepository, SportTypeRepository sportTypeRepository,
                             ParticipationRepository participationRepository) {
        this.userService = userService;
        this.tournamentRepository = tournamentRepository;
        this.sportTypeRepository = sportTypeRepository;
        this.participationRepository = participationRepository;
    }

    public Tournament createTournament(Tournament tournament, HttpServletRequest request) {
        User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UserNotAuthorizedException("Only a user with an admin role can create a tournament");
        }

        setSportTypeWithoutDuplication(tournament);

        tournament.setAudit(Audit.builder().build());
        Tournament savedTournament = tournamentRepository.save(tournament);
        savedTournament.setAudit(Audit.builder()
            .createdDate(savedTournament.getAudit().getCreatedDate())
            .createdBy(savedTournament.getAudit().getCreatedBy())
            .lastModifiedDate(null)
            .lastModifiedBy(null)
            .build());
        return savedTournament;
    }

    public Page<Tournament> findAllTournaments(Pageable pageable) {
        return tournamentRepository.findAll(pageable);
    }

    public Optional<Tournament> findTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }

    @Transactional
    public Tournament partiallyUpdateTournamentById(Long id, Tournament tournament, HttpServletRequest request) {
        tournament.setId(id);

        return tournamentRepository.findById(id).map(fetchedTournament -> {
            User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);
            if (currentUser.getRole() != Role.ADMIN) {
                throw new UserNotAuthorizedException("Only a user with an admin role can update the tournament");
            }

            Optional.ofNullable(tournament.getName()).ifPresent(fetchedTournament::setName);

            checkTournamentDates(tournament, fetchedTournament);
            Optional.ofNullable(tournament.getStartDate()).ifPresent(fetchedTournament::setStartDate);
            Optional.ofNullable(tournament.getEndDate()).ifPresent(fetchedTournament::setEndDate);

            Optional.ofNullable(tournament.getDescription()).ifPresent(fetchedTournament::setDescription);
            Optional.ofNullable(tournament.getMinimumPlayersPerTeam())
                .ifPresent(fetchedTournament::setMinimumPlayersPerTeam);

            return tournamentRepository.save(fetchedTournament);
        }).orElseThrow(() -> new TournamentNotFoundException("There is no tournament with the provided id"));
    }

    public void deleteTournamentById(Long id, HttpServletRequest request) {
        Tournament tournament = tournamentRepository.findById(id).map(fetchedTournament -> {
            User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);

            if (!currentUser.getId().equals(fetchedTournament.getAudit().getCreatedBy())) {
                throw new UserNotAuthorizedException("Only the admin who created the tournament can delete it");
            }

            return fetchedTournament;
        }).orElseThrow(() -> new TournamentNotFoundException("There is no tournament with the provided id"));

        if (isTournamentHavingTeams(id)) {
            throw new TeamAlreadyInTournamentException("There are teams in the tournament");
        }

        tournament.setSportType(null);
        tournamentRepository.deleteById(id);
    }

    private boolean isTournamentHavingTeams(Long id) {
        return !participationRepository.findByTournamentId(id).isEmpty();
    }

    private void setSportTypeWithoutDuplication(Tournament tournament) {
        SportType tournamentSportType = null;

        if (tournament.getSportType() != null) {
            if (tournament.getSportType().getSportType() == null
                || tournament.getSportType().getSportType().isBlank()) {
                throw new MissingSportTypeException("Cannot create a tournament without a sport type");
            }
            tournamentSportType = sportTypeRepository.findBySportType(tournament.getSportType().getSportType());
        }

        if (tournamentSportType != null) {
            tournament.setSportType(tournamentSportType);
        }
    }

    private void checkTournamentDates(Tournament newTournament, Tournament oldTournament) {
        if (oldTournament.getEndDate().isBefore(LocalDateTime.now())) {
            throw new TournamentOverException("Cannot change anything for a tournament when it is over");
        }

        if (newTournament.getStartDate() != null && newTournament.getStartDate().isBefore(LocalDateTime.now())) {
            throw new InvalidStartEndDateForTournamentException(
                "The start date cannot be changed to an earlier date than today"
            );
        }

        if (!oldTournament.getStartDate().isAfter(LocalDateTime.now())
            && !oldTournament.getEndDate().isBefore(LocalDateTime.now())) {
            if (newTournament.getStartDate() != null) {
                throw new InvalidStartEndDateForTournamentException(
                    "The start date cannot be changed when the tournament is active"
                );
            }
        }

        if (newTournament.getStartDate() != null && newTournament.getEndDate() != null) {
            if (!newTournament.getStartDate().isBefore(newTournament.getEndDate())) {
                throw new InvalidStartEndDateForTournamentException("The start date cannot be after the end date");
            } else {
                oldTournament.setStartDate(newTournament.getStartDate());
            }
        }

        if (newTournament.getEndDate() != null && newTournament.getEndDate().isBefore(LocalDateTime.now())) {
            throw new InvalidStartEndDateForTournamentException(
                "The end date cannot be changed to an earlier date than today"
            );
        }

        if (newTournament.getEndDate() != null && newTournament.getEndDate().isBefore(oldTournament.getStartDate())) {
            throw new InvalidStartEndDateForTournamentException(
                "The end date cannot be before the start date"
            );
        }

    }

}
