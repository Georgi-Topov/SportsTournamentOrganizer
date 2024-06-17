package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {

    Set<Participation> findByTournamentId(Long tournamentId);

    Set<Participation> findByTeamId(Long teamId);

}
