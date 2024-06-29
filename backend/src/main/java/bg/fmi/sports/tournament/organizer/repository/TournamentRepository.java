package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Set<Tournament> findAllByCreatedDateAfterAndCreatedDateBefore(LocalDateTime start, LocalDateTime end);
}
