package bg.fmi.sports.tournament.organizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
public class TeamPartialResponseDto {

    @Null(message = "The id of the team must not be entered manually")
    private Long id;

    @NotNull(message = "The name of the team cannot be null")
    @NotBlank(message = "The name of the team must have at least 1 non-white space character")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamPartialResponseDto that = (TeamPartialResponseDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
