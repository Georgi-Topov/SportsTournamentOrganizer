package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipId> {

    Set<Membership> findByTeamId(Long teamId);

    Set<Membership> findByPlayerId(Long playerId);

    @Query(value = "SELECT m.player_id FROM membership m WHERE m.created_date > ?1 AND m.team_id = ?2",
        nativeQuery = true)
    Set<Long> findMemberPlayersFromMembershipCreatedDateAfterLastTournament(LocalDateTime date, Long teamId);

    @Query(value = "SELECT m.player_id FROM membership m WHERE m.team_id = ?1",
        nativeQuery = true)
    Set<Long> findMemberPlayersFromMembershipCreatedDate(Long teamId);

    @Query(value = "SELECT m.team_id" +
        " FROM membership m" +
        " WHERE m.created_date = (" +
        " SELECT MAX(m2.created_date)" +
        " FROM membership m2" +
        " WHERE m2.player_id = ?1" +
        " ) AND m.player_id = ?1", nativeQuery = true)
    Optional<Long> findLatestTeamForPlayer(Long playerId);

    @Query(value = "SELECT m.player_id FROM membership m" +
        " JOIN participation p ON m.team_id = p.team_id" +
        " WHERE m.created_date > p.created_date" +
        " AND m.player_id = ?1 AND m.team_id = ?2 AND p.tournament_id = ?3", nativeQuery = true)
    Optional<Long> findPlayerPresenceAfterLastTeamTournament(Long playerId, Long teamId, Long tournamentId);

    @Query(value = "SELECT m.* FROM membership m" +
        " JOIN participation p ON m.team_id = p.team_id" +
        " WHERE m.created_date IN (SELECT MAX(m2.created_date)" +
                                    " FROM membership m2" +
                                    " GROUP BY m2.player_id)" +
        " AND m.team_id = ?1 AND p.tournament_id = ?2" +
        " AND (m.created_date > (SELECT t.end_date FROM tournament t" +
        " WHERE t.id = p.tournament_id" +
        " AND t.end_date < CURRENT_TIMESTAMP)" +
        " OR m.created_date < (SELECT t.start_date FROM tournament t" +
        " WHERE t.id = p.tournament_id" +
        " AND t.end_date > CURRENT_TIMESTAMP)" +
        ")", nativeQuery = true)
    Set<Membership> findPlayersInTeam(Long teamId, Long tournamentId);

    @Query(value = "SELECT m.* FROM membership m" +
        " WHERE m.created_date IN (SELECT MAX(m2.created_date)" +
                                    " FROM membership m2" +
                                    " GROUP BY m2.player_id)" +
        " AND m.team_id = ?1", nativeQuery = true)
    Set<Membership> findPlayersInTeamWhenTeamHasNotParticipatedYet(Long teamId);

}
