package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.TestDataUtil;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.exception.InvalidStartEndDateForTournamentException;
import bg.fmi.sports.tournament.organizer.exception.MissingSportTypeException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentOverException;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    TournamentRepository tournamentRepository;

    @Mock
    SportTypeRepository sportTypeRepository;

    @Mock
    UserService userService;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    TournamentService tournamentService;

//    @Test
//    void testCreateTournamentWhenSportTypeIsNotSpecified() {
//        Tournament tournamentWithoutSportType = TestDataUtil.createTournament1Football();
//        tournamentWithoutSportType.setSportType(TestDataUtil.createInvalidSportType());
//        User userAdmin = TestDataUtil.createUserAdmin();
//
//        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userAdmin);
//
////        assertThrows(MissingSportTypeException.class,
////            () -> tournamentService.createTournament(tournamentWithoutSportType, request),
////            "Team with missing sport type cannot be created");
//        verify(tournamentRepository, never()).save(any());
//    }

//    @Test
//    void testCreateTournamentWhenSportTypeIsSpecified() {
//        Tournament expectedTournamentWithSportType = TestDataUtil.createTournament1Football();
//        User userAdmin = TestDataUtil.createUserAdmin();
//
//        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userAdmin);
//        when(sportTypeRepository.findBySportType("football")).thenReturn(TestDataUtil.createSportType1Football());
//        when(tournamentRepository.save(expectedTournamentWithSportType)).thenReturn(expectedTournamentWithSportType);
//
//        Tournament actualTournamentWithSportType =
//            tournamentService.createTournament(TestDataUtil.createTournament1Football(), request);
//
//        assertEquals(expectedTournamentWithSportType, actualTournamentWithSportType,
//            "Tournament has a sport type which is already present in the database");
//
////        verify(sportTypeRepository, times(1)).findBySportType("football");
////        verify(tournamentRepository, times(1)).save(expectedTournamentWithSportType);
//    }

    @Test
    void testFindAllTournaments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tournament> expectedTournaments = new PageImpl<>(
            List.of(TestDataUtil.createTournament1Football(), TestDataUtil.createTournament2Football()));
        when(tournamentRepository.findAll(pageable)).thenReturn(expectedTournaments);

        Page<Tournament> actualTournaments = tournamentService.findAllTournaments(pageable);

        assertEquals(expectedTournaments, actualTournaments,
            "The tournaments persisted in the database should be returned");

        verify(tournamentRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindTournamentByIdWhenTournamentIsPresent() {
        Long tournamentId = 1L;
        Tournament expectedTournament = TestDataUtil.createTournament1Football();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(expectedTournament));

        Optional<Tournament> actualTournament = tournamentService.findTournamentById(tournamentId);

        assertEquals(expectedTournament, actualTournament.get(),
            "The tournament persisted in the database should be returned");

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void testPartiallyUpdateTournamentByIdWhenTournamentDoesNotExist() {
        Long tournamentId = 1L;
        Tournament tournament = TestDataUtil.createTournament1Football();

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        assertThrows(TournamentNotFoundException.class,
            () -> tournamentService.partiallyUpdateTournamentById(tournamentId, tournament, request),
            "The tournament does not exist");

        verify(tournamentRepository, times(1)).findById(tournamentId);
    }

    @Test
    void testPartiallyUpdateTournamentByIdWhenStartDateIsAfterEndDate() {
        Long tournamentId = 1L;
        Tournament giventournament = TestDataUtil.createTournament4InvalidStartEndDate();
        Tournament fetchedTOurnament = TestDataUtil.createTournament1Football();
        User userAdmin = TestDataUtil.createUserAdmin();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userAdmin);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(fetchedTOurnament));

        assertThrows(InvalidStartEndDateForTournamentException.class,
            () -> tournamentService.partiallyUpdateTournamentById(tournamentId, giventournament, request),
            "Start date cannot be after end date");

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void testPartiallyUpdateTournamentByIdWhenTournamentIsOver() {
        Long tournamentId = 5L;
        Tournament giventournament = TestDataUtil.createTournament1Football();
        giventournament.setEndDate(LocalDateTime.of(2025, 7, 7, 12, 0, 0));
        Tournament fetchedTournament = TestDataUtil.createTournament5Finished();
        User userAdmin = TestDataUtil.createUserAdmin();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userAdmin);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(fetchedTournament));

        assertThrows(TournamentOverException.class,
            () -> tournamentService.partiallyUpdateTournamentById(tournamentId, giventournament, request),
            "Cannot change tournament when it is over");

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void testPartiallyUpdateTournamentByIdWhenStartDateIsBeforeCurrentDate() {
        Long tournamentId = 2L;
        Tournament giventournament = TestDataUtil.createTournament1Football();
        giventournament.setStartDate(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
        Tournament fetchedTournament = TestDataUtil.createTournament2Football();
        User userAdmin = TestDataUtil.createUserAdmin();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userAdmin);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(fetchedTournament));

        assertThrows(InvalidStartEndDateForTournamentException.class,
            () -> tournamentService.partiallyUpdateTournamentById(tournamentId, giventournament, request),
            "Start date cannot be before current date");

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void testPartiallyUpdateTournamentByIdWhenTournamentExists() {
        Long tournamentId = 2L;
        Tournament givenTournament = TestDataUtil.createTournament1Football();
        Tournament expectedTournament = TestDataUtil.createTournament2Football();
        User userAdmin = TestDataUtil.createUserAdmin();

        when(userService.getUserFromTokenInAuthorizationHeader(request)).thenReturn(userAdmin);
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(expectedTournament));
        Optional.ofNullable(givenTournament.getName()).ifPresent(expectedTournament::setName);
        Optional.ofNullable(givenTournament.getStartDate()).ifPresent(expectedTournament::setStartDate);
        Optional.ofNullable(givenTournament.getEndDate()).ifPresent(expectedTournament::setEndDate);
        Optional.ofNullable(givenTournament.getDescription()).ifPresent(expectedTournament::setDescription);
        Optional.ofNullable(givenTournament.getMinimumPlayersPerTeam()).ifPresent(expectedTournament::setMinimumPlayersPerTeam);

        when(tournamentRepository.save(expectedTournament)).thenReturn(expectedTournament);

        Tournament actualTournament =
            tournamentService.partiallyUpdateTournamentById(tournamentId, givenTournament, request);

        assertEquals(expectedTournament, actualTournament, "The tournament is not updated successfully");

        verify(tournamentRepository, times(1)).findById(tournamentId);
        verify(tournamentRepository, times(1)).save(givenTournament);
    }

}
