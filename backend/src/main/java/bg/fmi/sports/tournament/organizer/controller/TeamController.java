package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.TeamDto;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.mapper.TeamMapper;
import bg.fmi.sports.tournament.organizer.service.TeamService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@Valid @RequestBody TeamDto teamDto) {
        Team team = teamMapper.dtoToTeam(teamDto);
        Team savedTeam = teamService.createTeam(team);
        return new ResponseEntity<>(teamMapper.teamToDto(savedTeam), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TeamDto>> findAllTeams(Pageable pageable) {
        Page<Team> fetchedTeams = teamService.findAllTeams(pageable);
        return new ResponseEntity<>(fetchedTeams.map(teamMapper::teamToDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> findTeamById(@PathVariable Long id) {
        return teamService.findTeamById(id)
            .map(fetchedTeam -> {
                TeamDto teamDto = teamMapper.teamToDto(fetchedTeam);
                return new ResponseEntity<>(teamDto, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TeamDto> partiallyUpdateTeamById(
        @PathVariable Long id, @RequestBody TeamDto teamDto) {

        Team team = teamMapper.dtoToTeam(teamDto);
        Team modifiedTeam = teamService.partiallyUpdateTeamById(id, team);

        return new ResponseEntity<>(teamMapper.teamToDto(modifiedTeam), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TeamDto> deleteTeamById(@PathVariable Long id) {
        teamService.deleteTeamById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // todo(maybe) : implement an endpoint to remove player from team (from membership table)

}
