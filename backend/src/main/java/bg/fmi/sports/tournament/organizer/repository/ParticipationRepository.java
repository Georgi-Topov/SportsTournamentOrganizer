package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {

    Set<Participation> findByTournamentId(Long tournamentId);

    Set<Participation> findByTeamId(Long teamId);

    @Query(value = "SELECT p.tournament_id" +
        " FROM participation p" +
        " WHERE p.created_date = (" +
        " SELECT MAX(p2.created_date)" +
        " FROM participation p2" +
        " WHERE p2.team_id = ?1" +
        " ) AND p.team_id = ?1", nativeQuery = true)
    Optional<Long> findLatestTournamentForTeam(Long teamId);

}
