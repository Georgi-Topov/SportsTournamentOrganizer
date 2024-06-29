package bg.fmi.sports.tournament.organizer.entity;

import bg.fmi.sports.tournament.organizer.entity.embedded.MembershipId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "membership")
@EntityListeners(AuditingEntityListener.class)
public class Membership {

    @EmbeddedId
    @AttributeOverride(name = "audit.createdDate", column = @Column(name = "created_date"))
    @AttributeOverride(name = "audit.lastModifiedDate", column = @Column(name = "last_modified_date"))
    @AttributeOverride(name = "audit.createdBy", column = @Column(name = "created_by"))
    @AttributeOverride(name = "audit.lastModifiedBy", column = @Column(name = "last_modified_by"))
    private MembershipId id;

    @NotNull(message = "The team to have a player assigned to it cannot be unknown")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;

    @NotNull(message = "Assigned player cannot be unknown")
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId")
    @JoinColumn(name = "player_id")
    private Player player;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Membership that = (Membership) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
