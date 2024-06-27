package bg.fmi.sports.tournament.organizer.dto;

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
public class TeamDto {

    private Long id;

    @NotNull(message = "The name of the team cannot be missing")
    private String name;

    @NotNull(message = "The sport type of the team cannot be missing")
    private SportTypeDto sportType;

    // todo : add @ManyToOne relationship to the user(manager) who created the team

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamDto teamDto = (TeamDto) o;
        return Objects.equals(id, teamDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
