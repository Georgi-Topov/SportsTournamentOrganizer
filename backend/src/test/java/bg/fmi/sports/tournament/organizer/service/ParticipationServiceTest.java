package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.TestDataUtil;
import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TeamToTournamentBadCorrespondenceException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentOverException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParticipationServiceTest {

    @Mock
    MembershipRepository membershipRepository;

    @Mock
    TeamRepository teamRepository;

    @Mock
    ParticipationRepository participationRepository;

    @Mock
    TournamentRepository tournamentRepository;

    @InjectMocks
    ParticipationService participationService;

    @Test
    void registerTeamToTournamentWhenTournamentDoesNotExist() {
        Long teamId = 1L;
        Long tournamentId = 1L;

        when(tournamentRepository.findById(any())).thenThrow(TournamentNotFoundException.class);

        assertThrows(TournamentNotFoundException.class, () -> participationService.registerTeamToTournament(tournamentId, teamId),
            "Tournament is not present in the database");
        verify(participationRepository, never()).save(any());
    }

    @Test
    void registerTeamToTournamentWhenTeamDoesNotExist() {
        Long teamId = 1L;
        Long tournamentId = 1L;

        Tournament fetchedTournament = TestDataUtil.createTournament1Football();

        when(tournamentRepository.findById(any())).thenReturn(Optional.of(fetchedTournament));
        when(teamRepository.findById(any())).thenThrow(TeamNotFoundException.class);

        assertThrows(TeamNotFoundException.class, () -> participationService.registerTeamToTournament(tournamentId, teamId),
            "Team is not present in the database");
        verify(participationRepository, never()).save(any());
    }

    @Test
    void registerTeamToTournamentWhenTournamentHasAlreadyStarted() {
        Long teamId = 1L;
        Long tournamentId = 1L;

        Tournament fetchedTournament = TestDataUtil.createTournament1Football();
        fetchedTournament.setStartDate(LocalDateTime.now().minusDays(30));
        fetchedTournament.setEndDate(LocalDateTime.now().plusDays(1));

        Team fetchedTeam = TestDataUtil.createTeam1();

        when(tournamentRepository.findById(any())).thenReturn(Optional.of(fetchedTournament));
        when(teamRepository.findById(any())).thenReturn(Optional.of(fetchedTeam));

        assertThrows(TournamentOverException.class, () -> participationService.registerTeamToTournament(tournamentId, teamId),
            "Cannot register a team to a tournament which is in progress");
        verify(participationRepository, never()).save(any());
    }

    @Test
    void registerTeamToTournamentWhenSportTypeOfTeamDoesNotCorrespondToTournamentSportType() {
        Long teamId = 1L;
        Long tournamentId = 3L;

        Tournament fetchedTournament = TestDataUtil.createTournament3Tennis();
        fetchedTournament.setStartDate(LocalDateTime.now().plusDays(1));
        fetchedTournament.setEndDate(LocalDateTime.now().plusDays(30));

        Team fetchedTeam = TestDataUtil.createTeam1();

        when(tournamentRepository.findById(any())).thenReturn(Optional.of(fetchedTournament));
        when(teamRepository.findById(any())).thenReturn(Optional.of(fetchedTeam));

        assertThrows(TeamToTournamentBadCorrespondenceException.class,
            () -> participationService.registerTeamToTournament(tournamentId, teamId),
            "Cannot register a team to a tournament which has different sport type");
        verify(participationRepository, never()).save(any());
    }

    @Test
    void registerTeamToTournamentWhenTeamIsAlreadyAssignedToAnotherActiveTournament() {
        Long teamId = 1L;
        Long tournamentId = 1L;
        Long latestTournamentId = 2L;

        Tournament fetchedTournament = TestDataUtil.createTournament1Football();
        fetchedTournament.setStartDate(LocalDateTime.now().plusDays(1));
        fetchedTournament.setEndDate(LocalDateTime.now().plusDays(30));
        fetchedTournament.setMinimumPlayersPerTeam(2);

        Team fetchedTeam = TestDataUtil.createTeam1();

        when(tournamentRepository.findById(any())).thenReturn(Optional.of(fetchedTournament));
        when(teamRepository.findById(any())).thenReturn(Optional.of(fetchedTeam));
        when(participationRepository.findLatestTournamentForTeam(teamId)).thenReturn(Optional.of(latestTournamentId));
        when(tournamentRepository.findById(latestTournamentId))
            .thenReturn(Optional.ofNullable(TestDataUtil.createTournament2Football()));

        assertThrows(TeamAlreadyInTournamentException.class,
            () -> participationService.registerTeamToTournament(tournamentId, teamId),
            "Team is registered to a tournament which is active");

        verify(participationRepository, never()).save(any());
    }

    @Test
    void registerTeamToTournamentWhenTheLastTournamentOfTeamIsNotFinishedAndTeamHasNotEnoughPlayersToParticipate() {
        Long teamId = 1L;
        Long tournamentId = 1L;

        Tournament fetchedTournament = TestDataUtil.createTournament1Football();
        fetchedTournament.setStartDate(LocalDateTime.now().plusDays(1));
        fetchedTournament.setEndDate(LocalDateTime.now().plusDays(30));
        fetchedTournament.setMinimumPlayersPerTeam(2);

        Team fetchedTeam = TestDataUtil.createTeam1();

        when(tournamentRepository.findById(any())).thenReturn(Optional.of(fetchedTournament));
        when(teamRepository.findById(any())).thenReturn(Optional.of(fetchedTeam));
        when(participationRepository.findLatestTournamentForTeam(teamId))
            .thenReturn(Optional.of(5L));
        when(tournamentRepository.findById(5L)).thenReturn(Optional.of(TestDataUtil.createTournament5Finished()));
        when(membershipRepository.findMemberPlayersFromMembershipCreatedDateAfterLastTournament(any(), any()))
            .thenReturn(Set.of(TestDataUtil.createPlayer1().getId()));

        assertThrows(TeamToTournamentBadCorrespondenceException.class,
            () -> participationService.registerTeamToTournament(tournamentId, teamId),
            "Cannot register a team to a tournament when the team does not have enough players in it");

        verify(participationRepository, never()).save(any());
    }

    @Test
    void registerTeamToTournamentWhenTeamHasTheRightToParticipateInTournament() {
        Long teamId = 1L;
        Long tournamentId = 1L;

        Tournament fetchedTournament = TestDataUtil.createTournament1Football();
        fetchedTournament.setStartDate(LocalDateTime.now().plusDays(1));
        fetchedTournament.setEndDate(LocalDateTime.now().plusDays(30));
        fetchedTournament.setMinimumPlayersPerTeam(2);

        Team fetchedTeam = TestDataUtil.createTeam1();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(fetchedTournament));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(fetchedTeam));
        when(participationRepository.findLatestTournamentForTeam(teamId))
            .thenReturn(Optional.empty());
        when(membershipRepository.findMemberPlayersFromMembershipCreatedDate(teamId))
            .thenReturn(Set.of(TestDataUtil.createPlayer1().getId(), TestDataUtil.createPlayer2().getId()));
        when(participationRepository.existsById(any())).thenReturn(false);

        participationService.registerTeamToTournament(tournamentId, teamId);

        verify(participationRepository, times(1))
            .save(Participation.builder()
                .id(ParticipationId.builder().tournamentId(tournamentId).teamId(teamId).build())
                .tournament(fetchedTournament).team(fetchedTeam).audit(new Audit()).build());
    }
}
