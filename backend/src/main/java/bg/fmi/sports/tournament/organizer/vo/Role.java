package bg.fmi.sports.tournament.organizer.vo;

import bg.fmi.sports.tournament.organizer.exceptions.InvalidRoleException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

public enum Role {
    ADMIN("admin"),
    USER("user"),
    MANAGER("manager");

    @Getter
    private String name;
    private Map<String, Role> nameToEnum;

    Role(String name) {
        this.name = name;
    }

    public static Role getEnumByName(String name) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getName().equals(name))
                .findAny()
                .orElseThrow(InvalidRoleException::new);
    }
}
