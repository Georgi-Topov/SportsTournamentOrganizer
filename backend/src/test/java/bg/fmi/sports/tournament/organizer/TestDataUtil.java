package bg.fmi.sports.tournament.organizer;

import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.vo.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDataUtil {
    private TestDataUtil() {
    }

    public static Player createPlayer1() {
        return Player.builder()
            .id(1L)
            .firstName("Kylian")
            .lastName("Mbappe")
            .birthdate(LocalDate.of(1997, 4, 14))
            .audit(creationAudit())
            .build();
    }

    public static Player createPlayer2() {
        return Player.builder()
            .id(2L)
            .firstName("Lamine")
            .lastName("Yamal")
            .birthdate(LocalDate.of(2007, 4, 14))
            .audit(creationAudit())
            .build();
    }

    public static Audit creationAudit() {
        return Audit.builder()
            .createdDate(LocalDateTime.of(2024, 6, 20, 12, 0, 0))
            .createdBy(1L)
            .build();
    }

    public static User createUserManager() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.MANAGER);
        return user;
    }

    public static User createUserAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ADMIN);
        return user;
    }

    public static Team createTeam1() {
        return Team.builder()
            .id(1L)
            .name("Barcelona")
            .sportType(createSportType1Football())
            .audit(creationAudit())
            .build();
    }

    public static Team createTeam2() {
        return Team.builder()
            .id(2L)
            .name("Real Madrid")
            .sportType(createSportType1Football())
            .audit(creationAudit())
            .build();
    }

    public static Team createTeam3() {
        return Team.builder()
            .id(3L)
            .name("Germany")
            .sportType(createSportType1Football())
            .audit(creationAudit())
            .build();
    }

    public static Team createTeam4() {
        return Team.builder()
            .id(4L)
            .name("France")
            .sportType(createSportType1Football())
            .audit(creationAudit())
            .build();
    }

    public static Tournament createTournament1Football() {
        return Tournament.builder()
            .id(1L)
            .name("euro 2024")
            .sportType(createSportType1Football())
            .startDate(LocalDateTime.of(2024, 7, 5, 16, 0, 0))
            .endDate(LocalDateTime.of(2024, 8, 5, 16, 0, 0))
            .description("Tournament")
            .minimumPlayersPerTeam(11)
            .audit(creationAudit())
            .build();
    }

    public static Tournament createTournament2Football() {
        return Tournament.builder()
            .id(2L)
            .name("uefa champions league 2024")
            .sportType(createSportType1Football())
            .startDate(LocalDateTime.of(2024, 7, 6, 16, 0, 0))
            .endDate(LocalDateTime.of(2024, 8, 6, 16, 0, 0))
            .description("Tournament")
            .minimumPlayersPerTeam(11)
            .audit(creationAudit())
            .build();
    }

    public static Tournament createTournament3Tennis() {
        return Tournament.builder()
            .id(3L)
            .name("roland-garros")
            .sportType(createSportType2Tennis())
            .startDate(LocalDateTime.of(2024, 7, 5, 16, 0, 0))
            .endDate(LocalDateTime.of(2024, 8, 5, 16, 0, 0))
            .description("Tournament")
            .minimumPlayersPerTeam(1)
            .audit(creationAudit())
            .build();
    }

    public static Tournament createTournament4InvalidStartEndDate() {
        return Tournament.builder()
            .id(4L)
            .name("tour")
            .sportType(createSportType1Football())
            .startDate(LocalDateTime.of(2024, 7, 5, 16, 0, 0))
            .endDate(LocalDateTime.of(2024, 6, 5, 16, 0, 0))
            .description("Tournament")
            .minimumPlayersPerTeam(1)
            .audit(creationAudit())
            .build();
    }

    public static Tournament createTournament5Finished() {
        return Tournament.builder()
            .id(5L)
            .name("uefa champions league 2023")
            .sportType(createSportType1Football())
            .startDate(LocalDateTime.of(2023, 7, 5, 16, 0, 0))
            .endDate(LocalDateTime.of(2023, 6, 5, 16, 0, 0))
            .description("Tournament")
            .minimumPlayersPerTeam(1)
            .audit(creationAudit())
            .build();
    }

    public static SportType createSportType1Football() {
        return SportType.builder()
            .id(1L)
            .sportType("football")
            .build();
    }

    public static SportType createSportType2Tennis() {
        return SportType.builder()
            .id(2L)
            .sportType("tennis")
            .build();
    }

    public static SportType createInvalidSportType() {
        return SportType.builder()
            .id(4L)
            .sportType(null)
            .build();
    }

}
