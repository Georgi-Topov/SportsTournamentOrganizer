package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.TournamentVenues;
import bg.fmi.sports.tournament.organizer.entity.embedded.TournamentVenuesKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentVenuesRepository extends JpaRepository<TournamentVenues, TournamentVenuesKey> {
}
