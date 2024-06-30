package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.TeamDto;
import bg.fmi.sports.tournament.organizer.dto.TeamPartialResponseDto;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.mapper.TeamMapper;
import bg.fmi.sports.tournament.organizer.repository.UserRepository;
import bg.fmi.sports.tournament.organizer.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService;
        this.teamMapper = teamMapper;
    }

    @PostMapping
    public ResponseEntity<TeamDto> createTeam(@NotNull HttpServletRequest request,
                                              @Valid @RequestBody TeamDto teamDto) {
        Team team = teamMapper.dtoToTeam(teamDto);
        Team savedTeam = teamService.createTeam(team, request);
        return new ResponseEntity<>(teamMapper.teamToDto(savedTeam), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TeamPartialResponseDto>> findAllTeams(Pageable pageable) {
        Page<Team> fetchedTeams = teamService.findAllTeams(pageable);
        return new ResponseEntity<>(fetchedTeams.map(teamMapper::teamToPartialResponseDto), HttpStatus.OK);
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
    public ResponseEntity<TeamDto> partiallyUpdateTeamById(@NotNull HttpServletRequest request,
                                                           @PathVariable Long id, @RequestBody TeamDto teamDto) {
        Team team = teamMapper.dtoToTeam(teamDto);
        Team modifiedTeam = teamService.partiallyUpdateTeamById(id, team, request);

        return new ResponseEntity<>(teamMapper.teamToDto(modifiedTeam), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TeamDto> deleteTeamById(@NotNull HttpServletRequest request, @PathVariable Long id) {
        teamService.deleteTeamById(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
