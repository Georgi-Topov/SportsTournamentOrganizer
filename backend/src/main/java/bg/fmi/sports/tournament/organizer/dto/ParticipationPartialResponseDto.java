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
public class ParticipationPartialResponseDto {

    @NotNull(message = "The tournament to have a team registered for it cannot be null")
    private TournamentPartialResponseDto tournament;

    @NotNull(message = "Registered team cannot be null")
    private TeamPartialResponseDto team;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationPartialResponseDto that = (ParticipationPartialResponseDto) o;
        return Objects.equals(tournament, that.tournament) && Objects.equals(team, that.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tournament, team);
    }

}
