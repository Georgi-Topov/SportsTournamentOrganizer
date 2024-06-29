package bg.fmi.sports.tournament.organizer.controller;

import bg.fmi.sports.tournament.organizer.dto.MembershipDto;
import bg.fmi.sports.tournament.organizer.dto.MembershipPartialResponseDto;
import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.mapper.MembershipMapper;
import bg.fmi.sports.tournament.organizer.service.AffiliationService;
import bg.fmi.sports.tournament.organizer.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<MembershipDto> assignPlayerToTeam(@NotNull HttpServletRequest request,
        @PathVariable Long teamId, @PathVariable Long playerId
    ) {
        Membership savedMembership = ((MembershipService)membershipService)
            .assignPlayerToTeam(teamId, playerId, request);
        return new ResponseEntity<>(membershipMapper.membershipToDto(savedMembership), HttpStatus.CREATED);
    }

    @GetMapping("/{teamId}/players")
    public ResponseEntity<Set<MembershipPartialResponseDto>> findAllAssignedPlayers(@PathVariable Long teamId) {
        Set<Membership> fetchedMemberships = ((MembershipService)membershipService).findAllAssignedPlayers(teamId);

        Set<MembershipPartialResponseDto> membershipDto =
            fetchedMemberships.stream()
                .map(membershipMapper::membershipToPartialResponseDto)
                .collect(Collectors.toSet());

        return new ResponseEntity<>(membershipDto, HttpStatus.OK);
    }

}
