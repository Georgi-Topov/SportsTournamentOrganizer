package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Match;
import bg.fmi.sports.tournament.organizer.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findFirstByVenue_IdAndDateTimeEndIsBetweenOrDateTimeStartIsBetween(Long id,
                                                                                LocalDateTime dateTimeEnd, LocalDateTime dateTimeEnd2, LocalDateTime dateTimeStart,
                                                                                LocalDateTime dateTimeStart2);

    @Query(
            value = "select * from match m" +
                    "where m.venue_id = ?" +
                    "order by date_time_end" +
                    "fetch first 1 rows only",
            nativeQuery = true)
    Optional<Match> findTopByDateTimeEndAndVenueIdEqualId(Long id);
}
