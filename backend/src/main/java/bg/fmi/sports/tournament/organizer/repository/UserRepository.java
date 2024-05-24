package bg.fmi.sports.tournament.organizer.repository;

import bg.fmi.sports.tournament.organizer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
