package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipId> {

    Set<Membership> findByTeamId(Long teamId);

    Set<Membership> findByPlayerId(Long playerId);

}
