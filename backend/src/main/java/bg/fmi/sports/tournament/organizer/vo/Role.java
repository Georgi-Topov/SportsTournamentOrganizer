package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.exception.InvalidRoleException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Role {
    ADMIN("admin"),
    USER("user"),
    MANAGER("manager"),
    NONE("none");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role getEnumByName(String name) {
        if (name == null) {
            return NONE;
        }
        return Arrays.stream(Role.values())
                .filter(role -> role.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new InvalidRoleException("Invalid role name: " + name));
    }
}
