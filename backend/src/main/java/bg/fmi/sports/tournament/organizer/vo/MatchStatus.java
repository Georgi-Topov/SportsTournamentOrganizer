package bg.fmi.sports.tournament.organizer.vo;

import lombok.Getter;

@Getter
public enum MatchStatus {
    WAIT("wait"),
    WIN("win"),
    LOST("lost");

    private final String name;

    MatchStatus(String name) {
        this.name = name;
    }
}
