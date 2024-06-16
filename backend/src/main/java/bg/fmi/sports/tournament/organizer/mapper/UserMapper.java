package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.UserDto;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.vo.Role;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {Role.class})
@RequiredArgsConstructor
public abstract class UserMapper {

    @Mapping(target = "role", expression = "java(Role.getEnumByName(userDto.getRole()))")
    public abstract User dtoToUser(UserDto userDto);

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    public abstract UserDto userToDto(User user);
}