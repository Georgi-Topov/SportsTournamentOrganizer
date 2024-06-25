package bg.fmi.sports.tournament.organizer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class UserNotFoundException extends RuntimeException {
    private final String message;
    private Exception exception;
}
