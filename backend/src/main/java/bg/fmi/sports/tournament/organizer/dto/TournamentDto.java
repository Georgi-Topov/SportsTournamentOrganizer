package bg.fmi.sports.tournament.organizer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
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

    private Long id;

    @NotNull(message = "The name of the tournament cannot be missing")
    private String name;

    @NotNull(message = "The sport type of the tournament cannot be missing")
    private SportTypeDto sportType;

    @NotNull(message = "The start date of the tournament cannot be missing")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy,HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "The end date of the tournament cannot be missing")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy,HH:mm:ss")
    private LocalDateTime endDate;

    private String description;

    @NotNull(message = "The minimum number of players of a participating team cannot be missing")
    private Integer minimumPlayersPerTeam;

    private Integer maximumPlayersPerTeam;

    // todo : add @ManyToOne relationship to the user(admin) who created the tournament

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private Long version;

    @AssertTrue(message = "The start date must be before the end date")
    public boolean isStartDateBeforeEndDate() {
        return startDate.isBefore(endDate);
    }

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
