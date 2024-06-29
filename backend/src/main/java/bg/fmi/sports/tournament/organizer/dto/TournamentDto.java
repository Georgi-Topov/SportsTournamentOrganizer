package bg.fmi.sports.tournament.organizer.dto;

import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentDto {

    @Null(message = "The id of the tournament must not be entered manually")
    private Long id;

    @NotNull(message = "The name of the tournament cannot be null")
    private String name;

    @NotNull(message = "The sport type of the tournament cannot be null")
    private SportTypeDto sportType;

    @NotNull(message = "The start date of the tournament cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy,HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "The end date of the tournament cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy,HH:mm:ss")
    private LocalDateTime endDate;

    private String description;

    @NotNull(message = "The minimum number of players of a participating team cannot be null")
    private Integer minimumPlayersPerTeam;

    @Embedded
    private Audit audit;

    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentDto that = (TournamentDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
