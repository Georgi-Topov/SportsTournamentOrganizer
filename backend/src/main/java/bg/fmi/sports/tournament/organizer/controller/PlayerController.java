package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.PlayerDto;
import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.mapper.PlayerMapper;
import bg.fmi.sports.tournament.organizer.service.PlayerService;
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
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerMapper playerMapper;

    public PlayerController(PlayerService playerService, PlayerMapper playerMapper) {
        this.playerService = playerService;
        this.playerMapper = playerMapper;
    }

    @PostMapping
    public ResponseEntity<PlayerDto> createPlayer(@Valid @RequestBody PlayerDto playerDto) {
        Player player = playerMapper.dtoToPlayer(playerDto);
        Player savedPlayer = playerService.createPlayer(player);
        return new ResponseEntity<>(playerMapper.playerToDto(savedPlayer), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PlayerDto>> findAllPlayers(Pageable pageable) {
        Page<Player> fetchedPlayers = playerService.findAllPlayers(pageable);
        return new ResponseEntity<>(fetchedPlayers.map(playerMapper::playerToDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDto> findPlayerById(@PathVariable Long id) {
        return playerService.findPlayerById(id)
            .map(fetchedPlayer -> {
                PlayerDto playerDto = playerMapper.playerToDto(fetchedPlayer);
                return new ResponseEntity<>(playerDto, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlayerDto> partiallyUpdatePlayerById(
        @PathVariable Long id, @RequestBody PlayerDto playerDto) {

        Player player = playerMapper.dtoToPlayer(playerDto);
        Player modifiedPlayer = playerService.partiallyUpdatePlayerById(id, player);

        return new ResponseEntity<>(playerMapper.playerToDto(modifiedPlayer), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PlayerDto> deletePlayerById(@PathVariable Long id) {
        playerService.deletePlayerById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
