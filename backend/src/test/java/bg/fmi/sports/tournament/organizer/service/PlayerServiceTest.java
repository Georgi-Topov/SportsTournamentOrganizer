package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.TestDataUtil;
import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.PlayerDuplicationException;
import bg.fmi.sports.tournament.organizer.exception.PlayerNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    PlayerRepository playerRepository;

    @Mock
    MembershipRepository membershipRepository;

    @Mock
    UserService userService;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    PlayerService playerService;

    @Test
    void testCreatePlayerWhenPlayerIsNotDuplicated() {
        Player expectedPlayer = TestDataUtil.createPlayer1();
        User userManager = TestDataUtil.createUserManager();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userManager);
        when(playerRepository.save(expectedPlayer)).thenReturn(expectedPlayer);

        Player actualPlayer = playerService.createPlayer(expectedPlayer, request);

        assertEquals(expectedPlayer, actualPlayer,
            "The player which is persisted in the database should be returned");

        verify(playerRepository, times(1)).save(expectedPlayer);
    }

    @Test
    void testCreatePlayerWhenPlayerIsDuplicated() {
        Player expectedPlayer = TestDataUtil.createPlayer1();
        User userManager = TestDataUtil.createUserManager();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userManager);
        when(playerRepository.save(expectedPlayer)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(PlayerDuplicationException.class,
            () -> playerService.createPlayer(expectedPlayer, request),
            "Player with the provided first name, last name and birthdate already exists");

        verify(playerRepository, times(1)).save(expectedPlayer);
    }

    @Test
    void testFindAllPlayers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Player> expectedPlayers = new PageImpl<>(
            List.of(TestDataUtil.createPlayer1(), TestDataUtil.createPlayer2()));
        when(playerRepository.findAll(pageable)).thenReturn(expectedPlayers);

        Page<Player> actualPlayers = playerService.findAllPlayers(pageable);

        assertEquals(expectedPlayers, actualPlayers,
            "The players persisted in the database should be returned");

        verify(playerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindPlayerByIdWhenPlayerIsPresent() {
        Long playerId = 1L;
        Player expectedPlayer = TestDataUtil.createPlayer1();

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(expectedPlayer));
        Optional<Player> actualPlayer = playerService.findPlayerById(playerId);

        assertEquals(expectedPlayer, actualPlayer.get(),
            "The players persisted in the database should be returned");

        verify(playerRepository, times(1)).findById(playerId);
    }

    @Test
    void testPartiallyUpdatePlayerByIdWhenPlayerDoesNotExist() {
        Long playerId = 1L;
        Player player = TestDataUtil.createPlayer2();
        User userManager = TestDataUtil.createUserManager();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userManager);
        when(membershipRepository.findLatestTeamForPlayer(playerId)).thenReturn(Optional.empty());
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class,
            () -> playerService.partiallyUpdatePlayerById(playerId, player, request),
            "The player does not exist");

        verify(playerRepository, times(1)).findById(playerId);
    }

    @Test
    void testPartiallyUpdatePlayerByIdWhenPlayerExists() {
        Long playerId = 2L;
        Player givenPlayer = TestDataUtil.createPlayer1();
        Player expectedPlayer = TestDataUtil.createPlayer2();
        User userManager = TestDataUtil.createUserManager();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userManager);
        when(membershipRepository.findLatestTeamForPlayer(playerId)).thenReturn(Optional.empty());

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(expectedPlayer));
        Optional.ofNullable(givenPlayer.getFirstName()).ifPresent(expectedPlayer::setFirstName);
        Optional.ofNullable(givenPlayer.getLastName()).ifPresent(expectedPlayer::setLastName);
        Optional.ofNullable(givenPlayer.getBirthdate()).ifPresent(expectedPlayer::setBirthdate);
        Optional.ofNullable(givenPlayer.getGender()).ifPresent(expectedPlayer::setGender);
        Optional.ofNullable(givenPlayer.getWeight()).ifPresent(expectedPlayer::setWeight);
        when(playerRepository.save(expectedPlayer)).thenReturn(expectedPlayer);

        Player actualPlayer = playerService.partiallyUpdatePlayerById(playerId, givenPlayer, request);

        assertEquals(expectedPlayer, actualPlayer, "The player is not updated successfully");

        verify(playerRepository, times(1)).findById(playerId);
        verify(playerRepository, times(1)).save(givenPlayer);
    }

    @Test
    void testDeletePlayerByIdWhenPlayerIsAssignedToTeam() {
        Long playerId = 1L;
        Player playerToBeDeleted = TestDataUtil.createPlayer1();
        Team team = TestDataUtil.createTeam1();
        User userManager = TestDataUtil.createUserManager();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userManager);
        when(membershipRepository.findByPlayerId(playerId)).
            thenReturn(Set.of(Membership.builder().player(playerToBeDeleted).team(team).build()));

        assertThrows(PlayerAlreadyInTeamException.class,
            () -> playerService.deletePlayerById(playerId, request),
            "Assigned to a team player cannot be deleted");

        verify(membershipRepository, times(1)).findByPlayerId(playerId);
        verify(playerRepository, never()).deleteById(any());
    }

    @Test
    void testDeletePlayerByIdWhenPlayerIsNotAssignedToTeam() {
        Long playerId = 1L;
        Set<Membership> noMemberships = Set.of();
        User userManager = TestDataUtil.createUserManager();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userManager);
        when(membershipRepository.findByPlayerId(playerId)).thenReturn(noMemberships);

        playerService.deletePlayerById(playerId, request);

        verify(membershipRepository, times(1)).findByPlayerId(playerId);
        verify(playerRepository, times(1)).deleteById(playerId);
    }

}
