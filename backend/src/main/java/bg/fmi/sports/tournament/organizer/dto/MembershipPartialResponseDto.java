package bg.fmi.sports.tournament.organizer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipPartialResponseDto {

    @NotNull(message = "The team to have a player assigned to it cannot be null")
    private TeamPartialResponseDto team;

    @NotNull(message = "Assigned player cannot be null")
    private PlayerPartialResponseDto player;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipPartialResponseDto that = (MembershipPartialResponseDto) o;
        return Objects.equals(team, that.team) && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, player);
    }

}
