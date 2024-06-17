package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Membership;
import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public void addPlayerToTeam(Team team, Player player) {
        MembershipId membershipId = new MembershipId(team.getId(), player.getId());

        Membership membership = Membership.builder()
            .id(membershipId)
            .team(team)
            .player(player)
            .audit(new Audit())
            .build();

        if (membershipRepository.existsById(new MembershipId(team.getId(), player.getId()))) {
            throw new PlayerAlreadyInTeamException("The player is already assigned to the team");
        }

        membershipRepository.save(membership);
    }

    public Page<Membership> findAllMemberships(Pageable pageable) {
        return membershipRepository.findAll(pageable);
    }

    public boolean isPlayerAssigned(Long id) {
        return !membershipRepository.findByPlayerId(id).isEmpty();
    }

    public boolean isTeamRegistered(Long id) {
        return !membershipRepository.findByTeamId(id).isEmpty();
    }

}
