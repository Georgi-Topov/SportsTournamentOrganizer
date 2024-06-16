package bg.fmi.sports.tournament.organizer.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class InvalidRoleException extends RuntimeException {
    private final String message;
    private Exception exception;
}
