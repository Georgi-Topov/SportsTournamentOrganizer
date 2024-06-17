package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.SportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportTypeRepository extends JpaRepository<SportType, Long> {

    SportType findBySportType(String name);

    SportType findBySportTypeAndMinimumPlayersAndMaximumPlayers(String name,
                                                                Integer minimumPlayers, Integer maximumPlayers);

}
