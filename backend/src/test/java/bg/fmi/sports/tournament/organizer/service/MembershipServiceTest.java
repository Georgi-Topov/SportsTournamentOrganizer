package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.TestDataUtil;
import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import bg.fmi.sports.tournament.organizer.exception.PlayerNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import jakarta.persistence.Embedded;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @Mock
    MembershipRepository membershipRepository;

    @Mock
    ParticipationRepository participationRepository;

    @Mock
    TeamRepository teamRepository;

    @Mock
    PlayerRepository playerRepository;

    @Mock
    UserService userService;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    MembershipService membershipService;

    @Test
    void testAssignPlayerToTeamWhenTeamDoesNotExist() {
        Long teamId = 1L;
        Long playerId = 1L;

        when(teamRepository.findById(any())).thenThrow(TeamNotFoundException.class);

        assertThrows(TeamNotFoundException.class,
            () -> membershipService.assignPlayerToTeam(teamId, playerId, request),
            "Team is not present in the database");
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void testRegisterPlayerToTeamWhenPlayerDoesNotExist() {
        Long teamId = 1L;
        Long playerId = 1L;
        Team fetchedTeam = TestDataUtil.createTeam1();

        when(teamRepository.findById(any())).thenReturn(Optional.of(fetchedTeam));
        when(playerRepository.findById(any())).thenThrow(PlayerNotFoundException.class);

        assertThrows(PlayerNotFoundException.class,
            () -> membershipService.assignPlayerToTeam(teamId, playerId, request),
            "Player is not present in the database");
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void testAssignPlayerToTeamWhenPlayerHasTheRightToBeMemberOfTeam() {
        Long teamId = 1L;
        Long playerId = 1L;

        Player fetchedPlayer = TestDataUtil.createPlayer1();
        Team fetchedTeam = TestDataUtil.createTeam1();
        User userManager = TestDataUtil.createUserManager();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userManager);
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(fetchedTeam));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(fetchedPlayer));
        when(membershipRepository.findLatestTeamForPlayer(playerId)).thenReturn(Optional.empty());
        when(participationRepository.findLatestTournamentForTeam(teamId)).thenReturn(Optional.empty());

        membershipService.assignPlayerToTeam(teamId, playerId, request);

        verify(membershipRepository, times(1))
            .save(Membership.builder()
                .id(MembershipId.builder().playerId(playerId).teamId(teamId).audit(new Audit()).build())
                    .player(fetchedPlayer).team(fetchedTeam).build());
    }

}
