package bg.fmi.sports.tournament.organizer.entity;

import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
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
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;
import java.util.Set;

@Log
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "team")
@EntityListeners(AuditingEntityListener.class)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "The name of the team cannot be null")
    @NotBlank(message = "The name of the team must have at least 1 non-white space character")
    private String name;

    @NotNull(message = "The sport type of the team cannot be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sport_type_id")
    private SportType sportType;

    @Embedded
    private Audit audit;

    @Version
    private Long version;

    @OneToMany(mappedBy = "team")
    private Set<TeamNotification> notification;

    @OneToMany(mappedBy = "team")
    private Set<Match> matches;

    @OneToMany(mappedBy = "tournament")
    private Set<Participation> tournaments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
