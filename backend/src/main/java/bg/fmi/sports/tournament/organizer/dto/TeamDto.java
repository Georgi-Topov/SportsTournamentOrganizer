package bg.fmi.sports.tournament.organizer.dto;

import bg.fmi.sports.tournament.organizer.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
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
public class TeamDto {

    @Null(message = "The id of the team must not be entered manually")
    private Long id;

    @NotNull(message = "The name of the team cannot be null")
    @NotBlank(message = "The name of the team must have at least 1 non-white space character")
    private String name;

    @NotNull(message = "The sport type of the team cannot be null")
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
