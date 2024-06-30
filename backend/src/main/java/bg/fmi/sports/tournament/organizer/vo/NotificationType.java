package bg.fmi.sports.tournament.organizer.vo;

import lombok.Getter;

@Getter
public enum NotificationType {
    ALL("all"),
    O_IMPORTANT("only_important"),
    NONE("none");

    private final String name;

    NotificationType(String name) {
        this.name = name;
    }
}
