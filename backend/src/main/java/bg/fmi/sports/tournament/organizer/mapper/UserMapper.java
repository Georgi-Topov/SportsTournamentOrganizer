package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.UserDto;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.vo.Role;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", imports = {Role.class})
@RequiredArgsConstructor
public abstract class UserMapper {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mapping(target = "role", expression = "java(Role.getEnumByName(userDto.getRole()))")
    @Mapping(source ="password", target = "password", qualifiedByName = "encodePass")
    public abstract User dtoToUser(UserDto userDto);

    @Named("encodePass")
    protected String encodePass(String pass) {
        return passwordEncoder.encode(pass);
    }

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    public abstract UserDto userToDto(User user);
}