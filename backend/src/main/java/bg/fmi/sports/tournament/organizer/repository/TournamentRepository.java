package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
