package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.MembershipDto;
import bg.fmi.sports.tournament.organizer.dto.ParticipationDto;
import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.mapper.MembershipMapper;
import bg.fmi.sports.tournament.organizer.service.AffiliationService;
import bg.fmi.sports.tournament.organizer.service.MembershipService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teams")
public class MembershipController {

    private final AffiliationService membershipService;
    private final MembershipMapper membershipMapper;

    public MembershipController(@Qualifier("membership") AffiliationService membershipService,
                                MembershipMapper membershipMapper) {
        this.membershipService = membershipService;
        this.membershipMapper = membershipMapper;
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<ParticipationDto> assignPlayerToTeam(
        @PathVariable Long teamId, @PathVariable Long playerId
    ) {
        ((MembershipService)membershipService).assignPlayerToTeam(teamId, playerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/players")
    public ResponseEntity<Page<MembershipDto>> findAllMemberships(Pageable pageable) {
        Page<Membership> fetchedMemberships = ((MembershipService)membershipService).findAllMemberships(pageable);
        return new ResponseEntity<>(fetchedMemberships.map(membershipMapper::membershipToDto), HttpStatus.OK);
    }

}
