package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.SportTypeDto;
import bg.fmi.sports.tournament.organizer.entity.SportType;
import bg.fmi.sports.tournament.organizer.mapper.SportTypeMapper;
import bg.fmi.sports.tournament.organizer.repository.SportTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sport-type")
public class SportTypeController {
    private final SportTypeRepository sportTypeRepository;
    private final SportTypeMapper sportTypeMapper;

    public SportTypeController(SportTypeRepository sportTypeRepository, SportTypeMapper sportTypeMapper) {
        this.sportTypeRepository = sportTypeRepository;
        this.sportTypeMapper = sportTypeMapper;
    }

    @GetMapping
    public ResponseEntity<List<SportTypeDto>> getAllSportTypes() {
        return ResponseEntity.ok(sportTypeRepository.findAll().stream().map(sportTypeMapper::sportTypeToDto).toList());
    }
}
