package bg.fmi.sports.tournament.organizer.dto;

import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
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
public class ParticipationDto {

    private ParticipationId id;

    @NotNull(message = "The tournament to have a team registered for it cannot be null")
    private TournamentDto tournament;

    @NotNull(message = "Registered team cannot be null")
    private TeamDto team;

    private Audit audit;

    private Long version;

}
