package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.ParticipationDto;
import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.mapper.ParticipationMapper;
import bg.fmi.sports.tournament.organizer.service.AffiliationService;
import bg.fmi.sports.tournament.organizer.service.ParticipationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}/teams")
public class ParticipationController {

    private final AffiliationService participationService;
    private final ParticipationMapper participationMapper;

    public ParticipationController(@Qualifier("participation") ParticipationService participationService,
                                   ParticipationMapper participationMapper) {
        this.participationService = participationService;
        this.participationMapper = participationMapper;
    }

    @PostMapping("/{teamId}")
    public ResponseEntity<ParticipationDto> registerTeamToTournament(
        @PathVariable Long tournamentId, @PathVariable Long teamId
    ) {
        ((ParticipationService)participationService).registerTeamToTournament(tournamentId, teamId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Set<ParticipationDto>> findAllParticipatingTeams(@PathVariable Long tournamentId) {
        Set<Participation> fetchedTeams =
            ((ParticipationService)participationService).findAllParticipatingTeams(tournamentId);

        Set<ParticipationDto> fetchedDtoTeams = fetchedTeams.stream().map(participationMapper::participationToDto)
            .collect(Collectors.toSet());

        return new ResponseEntity<>(fetchedDtoTeams, HttpStatus.OK);
    }

    // todo : to have a method which gets the team which won the tournament
}
