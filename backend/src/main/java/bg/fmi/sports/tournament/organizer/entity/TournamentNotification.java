package bg.fmi.sports.tournament.organizer.entity;

import bg.fmi.sports.tournament.organizer.entity.embedded.TournamentNotificationKey;
import bg.fmi.sports.tournament.organizer.vo.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;

@Entity
@Data
public class TournamentNotification {

    @EmbeddedId
    private TournamentNotificationKey id;

    @ManyToOne
    @MapsId("tournament")
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;
}
