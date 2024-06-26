package bg.fmi.sports.tournament.organizer.entity;

import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.exception.InvalidStartEndDateForTournamentException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tournament")
@EntityListeners(AuditingEntityListener.class)
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "The name of the tournament cannot be null")
    private String name;

    @NotNull(message = "The sport type of the tournament cannot be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sport_type_id")
    private SportType sportType;

    @NotNull(message = "The start date of the tournament cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "The end date of the tournament cannot be null")
    private LocalDateTime endDate;

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (this.startDate.isAfter(this.endDate)) {
            throw new InvalidStartEndDateForTournamentException("Start date must be before end date");
        }
    }

    private String description;

    @NotNull(message = "The minimum number of players of a participating team cannot be null")
    private Integer minimumPlayersPerTeam;

    @Embedded
    private Audit audit;

    @Version
    private Long version;

    @OneToMany(mappedBy = "tournament")
    private Set<TournamentNotification> notification;

    @OneToMany(mappedBy = "tournament")
    private Set<TournamentVenues> venues;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tournament that = (Tournament) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
