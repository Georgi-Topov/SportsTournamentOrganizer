package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.TournamentDto;
import bg.fmi.sports.tournament.organizer.dto.TournamentPartialResponseDto;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.mapper.TournamentMapper;
import bg.fmi.sports.tournament.organizer.service.TournamentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentMapper tournamentMapper;

    public TournamentController(TournamentService tournamentService, TournamentMapper tournamentMapper) {
        this.tournamentService = tournamentService;
        this.tournamentMapper = tournamentMapper;
    }

    @PostMapping
    public ResponseEntity<TournamentDto> createTournament(@NotNull HttpServletRequest request,
                                                          @Valid @RequestBody TournamentDto tournamentDto) {
        Tournament tournament = tournamentMapper.dtoToTournament(tournamentDto);
        Tournament savedTournament = tournamentService.createTournament(tournament, request);
        return new ResponseEntity<>(tournamentMapper.tournamentToDto(savedTournament), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TournamentPartialResponseDto>> findAllTournaments(Pageable pageable) {
        Page<Tournament> fetchedTournaments = tournamentService.findAllTournaments(pageable);
        return new ResponseEntity<>(
            fetchedTournaments.map(tournamentMapper::tournamentToPartialResponseDto), HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDto> findTournamentById(@PathVariable Long id) {
        return tournamentService.findTournamentById(id)
            .map(fetchedTournament -> {
                TournamentDto tournamentDto = tournamentMapper.tournamentToDto(fetchedTournament);
                return new ResponseEntity<>(tournamentDto, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TournamentDto> partiallyUpdateTournamentById(@NotNull HttpServletRequest request,
        @PathVariable Long id, @RequestBody TournamentDto tournamentDto) {

        Tournament tournament = tournamentMapper.dtoToTournament(tournamentDto);
        Tournament modifiedTournament = tournamentService.partiallyUpdateTournamentById(id, tournament, request);

        return new ResponseEntity<>(tournamentMapper.tournamentToDto(modifiedTournament), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TournamentDto> deleteTournamentById(@NotNull HttpServletRequest request,
                                                              @PathVariable Long id) {
        tournamentService.deleteTournamentById(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
