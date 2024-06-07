package bg.fmi.sports.tournament.organizer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid role")
public class InvalidRoleException extends RuntimeException {
}
