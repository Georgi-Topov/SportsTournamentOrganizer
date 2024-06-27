package bg.fmi.sports.tournament.organizer.dto;

import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipDto {

    private MembershipId id;

    @NotNull(message = "The team to have a player assigned to it cannot be unknown")
    private TeamDto team;

    @NotNull(message = "Assigned player cannot be unknown")
    private PlayerDto player;

    private Audit audit;

    private Long version;

}
