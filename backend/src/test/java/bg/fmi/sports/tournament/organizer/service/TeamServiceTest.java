package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.TestDataUtil;
import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.exception.MissingSportTypeException;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class TeamServiceTest {

    @Mock
    TeamRepository teamRepository;

    @Mock
    MembershipRepository membershipRepository;

    @Mock
    ParticipationRepository participationRepository;

    @Mock
    SportTypeRepository sportTypeRepository;

    @InjectMocks
    TeamService teamService;

    @Test
    void testCreateTeamWhenSportTypeIsNotSpecified() {
        Team teamWithoutSportType = TestDataUtil.createTeam1();
        teamWithoutSportType.setSportType(TestDataUtil.createInvalidSportType());

        assertThrows(MissingSportTypeException.class, () -> teamService.createTeam(teamWithoutSportType),
            "Team with missing sport type cannot be created");
        verify(teamRepository, never()).save(any());
    }

    @Test
    void testCreateTeamWhenSportTypeIsSpecified() {
        Team expectedTeamWithSportType = TestDataUtil.createTeam2();

        when(sportTypeRepository.findBySportType("football")).thenReturn(TestDataUtil.createSportType1Football());
        when(teamRepository.save(expectedTeamWithSportType)).thenReturn(expectedTeamWithSportType);

        Team actualTeamWithSportType = teamService.createTeam(TestDataUtil.createTeam2());

        assertEquals(expectedTeamWithSportType, actualTeamWithSportType, "Team has a sport type which is" +
            "already present in the database");

        verify(sportTypeRepository, times(1)).findBySportType("football");
        verify(teamRepository, times(1)).save(expectedTeamWithSportType);
    }

    @Test
    void testFindAllTeams() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Team> expectedTeams = new PageImpl<>(
            List.of(TestDataUtil.createTeam3(), TestDataUtil.createTeam4()));
        when(teamRepository.findAll(pageable)).thenReturn(expectedTeams);

        Page<Team> actualTeams = teamService.findAllTeams(pageable);

        assertEquals(expectedTeams, actualTeams,
            "The teams persisted in the database should be returned");

        verify(teamRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindTeamByIdWhenTeamIsPresent() {
        Long teamId = 2L;
        Team expectedTeam = TestDataUtil.createTeam1();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(expectedTeam));

        Optional<Team> actualTeam = teamService.findTeamById(teamId);

        assertEquals(expectedTeam, actualTeam.get(),
            "The team persisted in the database should be returned");

        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void testPartiallyUpdateTeamByIdWhenTeamDoesNotExist() {
        Long teamId = 1L;
        Team team = TestDataUtil.createTeam2();

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> teamService.partiallyUpdateTeamById(teamId, team),
            "The team does not exist");

        verify(teamRepository, times(1)).findById(teamId);
    }

    @Test
    void testPartiallyUpdateTeamByIdWhenTeamExists() {
        Long teamId = 4L;
        Team givenTeam = TestDataUtil.createTeam3();
        Team expectedTeam = TestDataUtil.createTeam4();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(expectedTeam));
        Optional.ofNullable(givenTeam.getName()).ifPresent(expectedTeam::setName);
        when(teamRepository.save(expectedTeam)).thenReturn(expectedTeam);

        Team actualTeam = teamService.partiallyUpdateTeamById(teamId, givenTeam);

        assertEquals(expectedTeam, actualTeam, "The team is not updated successfully");

        verify(teamRepository, times(1)).findById(teamId);
        verify(teamRepository, times(1)).save(givenTeam);
    }

    @Test
    void testDeleteTeamByIdWhenTeamIsRegisteredToTournament() {
        Long teamId = 2L;
        Team teamToBeDeleted = TestDataUtil.createTeam2();
        Tournament tournament = TestDataUtil.createTournament1Football();

        when(participationRepository.findByTeamId(teamId)).
            thenReturn(Set.of(Participation.builder().team(teamToBeDeleted).tournament(tournament).build()));

        assertThrows(TeamAlreadyInTournamentException.class, () -> teamService.deleteTeamById(teamId),
            "Registered to a tournament team cannot be deleted");

        verify(participationRepository, times(1)).findByTeamId(teamId);
        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteTeamByIdWhenTeamHasPlayers() {
        Long teamId = 2L;
        Team teamToBeDeleted = TestDataUtil.createTeam2();
        Player player = TestDataUtil.createPlayer1();

        when(membershipRepository.findByTeamId(teamId))
            .thenReturn(Set.of(Membership.builder().team(teamToBeDeleted).player(player).build()));

        assertThrows(PlayerAlreadyInTeamException.class, () -> teamService.deleteTeamById(teamId),
            "Team having players assigned to it cannot be deleted");

        verify(membershipRepository, times(1)).findByTeamId(teamId);
        verify(teamRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteTeamByIdWhenPlayerIsNotAssignedToTeamAndTeamIsNotRegisteredToTournament() {
        Long teamId = 1L;
        Set<Membership> noMemberships = Set.of();
        Set<Participation> noParticipations = Set.of();

        when(membershipRepository.findByTeamId(teamId)).thenReturn(noMemberships);
        when(participationRepository.findByTeamId(teamId)).thenReturn(noParticipations);

        teamService.deleteTeamById(teamId);

        verify(membershipRepository, times(1)).findByTeamId(teamId);
        verify(participationRepository, times(1)).findByTeamId(teamId);
        verify(teamRepository, times(1)).deleteById(teamId);
    }



}
