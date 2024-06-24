package bg.fmi.sports.tournament.organizer.entity;

import bg.fmi.sports.tournament.organizer.entity.embedded.TeamNotificationKey;
import bg.fmi.sports.tournament.organizer.vo.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;

@Entity
@Data
public class TeamNotification {

    @EmbeddedId
    TeamNotificationKey id;

    @ManyToOne
    @MapsId("team")
    @JoinColumn(name = "team_id")
    Team team;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "type")
    NotificationType type;
}
