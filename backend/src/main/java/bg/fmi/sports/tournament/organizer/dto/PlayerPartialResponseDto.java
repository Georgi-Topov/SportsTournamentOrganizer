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
public class PlayerPartialResponseDto {

    @Null(message = "The id of the player must not be entered manually")
    private Long id;

    @NotNull(message = "Player's first name cannot be null")
    @NotBlank(message = "Player's first name must have at least 1 non-white space character")
    private String firstName;

    @NotNull(message = "Player's last name cannot be null")
    @NotBlank(message = "Player's last name must have at least 1 non-white space character")
    private String lastName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerPartialResponseDto that = (PlayerPartialResponseDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
