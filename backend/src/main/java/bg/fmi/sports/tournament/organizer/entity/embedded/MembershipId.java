package bg.fmi.sports.tournament.organizer.entity.embedded;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class MembershipId implements Serializable {

    private Long teamId;

    private Long playerId;

    @Embedded
    private Audit audit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipId that = (MembershipId) o;
        return Objects.equals(teamId, that.teamId) && Objects.equals(playerId, that.playerId) &&
            Objects.equals(audit, that.audit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, playerId, audit);
    }

}