package bg.fmi.sports.tournament.organizer.dto;

import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
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

    private TournamentDto tournament;

    private TeamDto team;

    private Audit audit;

    private Long version;

}
