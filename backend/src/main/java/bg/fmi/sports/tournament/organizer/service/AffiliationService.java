package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class AffiliationService {

    final ParticipationRepository participationRepository;
    final TournamentRepository tournamentRepository;

    public AffiliationService(ParticipationRepository participationRepository,
                              TournamentRepository tournamentRepository) {
        this.participationRepository = participationRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public Optional<Tournament> checkTeamParticipationStatus(Long teamId) {
        Optional<Tournament> latestTournament = Optional.empty();
        Optional<Long> latestTournamentId = participationRepository.findLatestTournamentForTeam(teamId);

        if (latestTournamentId.isPresent()) {
            latestTournament = tournamentRepository.findById(latestTournamentId.get());

            if (latestTournament.isPresent()) {
                if (LocalDateTime.now().isBefore(latestTournament.get().getEndDate())) {
                    throw new TeamAlreadyInTournamentException(
                        "The team is currently registered to a tournament with an id of "
                            + latestTournament.get().getId().toString() + " which is not over yet");
                }
            } else {
                throw new TournamentNotFoundException("The tournament was not found");
            }

        }

        return latestTournament;
    }

}
