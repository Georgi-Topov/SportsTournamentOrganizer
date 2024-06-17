package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;

    public ParticipationService(ParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public void addTeamToTournament(Tournament tournament, Team team) {
        ParticipationId participationId = new ParticipationId(tournament.getId(), team.getId());

        Participation participation = Participation.builder()
            .id(participationId)
            .tournament(tournament)
            .team(team)
            .audit(new Audit())
            .build();

        if (participationRepository.existsById(new ParticipationId(tournament.getId(), team.getId()))) {
            throw new TeamAlreadyInTournamentException("The team is already registered to the tournament");
        }

        participationRepository.save(participation);
    }

    public boolean isTeamRegistered(Long id) {
        return !participationRepository.findByTeamId(id).isEmpty();
    }

}
