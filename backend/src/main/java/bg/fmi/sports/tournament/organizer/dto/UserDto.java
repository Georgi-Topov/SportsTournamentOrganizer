package bg.fmi.sports.tournament.organizer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    @NotNull(message = "username cannot be null!")
    @Size(max = 64, min = 3, message = "username has invalid size")
    private String username;
//    @Size(min = 256 ,max = 256, message = "password has invalid size")
    private String password;
    @Email(message = "Email should be valid")
    private String email;
    @NotNull
    private String role;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

//    private Long createdBy;
//    private Long modifiedBy;

    private String token;
}
