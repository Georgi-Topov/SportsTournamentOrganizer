package bg.fmi.sports.tournament.organizer.entity.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class TournamentVenuesKey implements Serializable {
    @Column(name = "tournament_id")
    Long tournament;

    @Column(name = "venue_id")
    Long venue;
}
