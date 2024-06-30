package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query(value = "SELECT p.id FROM player p " +
        "WHERE p.first_name = ?1 AND p.last_name = ?2 AND p.birthdate = ?3", nativeQuery = true)
    Optional<Long> findByFirstNameAndLastNameAndBirthdate(String firstName, String lastName, LocalDate birthdate);

}
