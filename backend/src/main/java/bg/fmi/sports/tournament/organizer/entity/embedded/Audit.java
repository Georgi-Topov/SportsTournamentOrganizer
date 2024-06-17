package bg.fmi.sports.tournament.organizer.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
@EntityListeners(AuditingEntityListener.class)
public class Audit implements Serializable {

    @Column(insertable = true, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(insertable = false, updatable = true)
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column(insertable = true, updatable = false)
    private Long createdBy;

    @Column(insertable = false, updatable = true)
    private Long lastModifiedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audit audit = (Audit) o;
        return Objects.equals(createdDate, audit.createdDate) &&
            Objects.equals(createdBy, audit.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdDate, createdBy);
    }

}