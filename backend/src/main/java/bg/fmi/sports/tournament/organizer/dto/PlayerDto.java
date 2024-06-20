package bg.fmi.sports.tournament.organizer.dto;

import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDto {

    private Long id;

    @NotNull(message = "Player's first name cannot be missing")
    private String firstName;

    @NotNull(message = "Player's lsat name cannot be missing")
    private String lastName;

    @NotNull(message = "Player's birthdate cannot be missing")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthdate;

    private String gender;

    private BigDecimal weight;

    @Embedded
    private Audit audit;

    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerDto playerDto = (PlayerDto) o;
        return Objects.equals(id, playerDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
