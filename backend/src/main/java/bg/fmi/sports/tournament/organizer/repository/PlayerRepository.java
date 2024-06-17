package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
}
