package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.UserDto;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.vo.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {Role.class})
public interface UserMapper {

    @Mapping(target = "role", expression = "java(Role.getEnumByName(userDto.getRole()))")
    User dtoToUser(UserDto userDto);

    @Mapping(target = "role", expression = "java(user.getRole().getName())")
    UserDto userToDto(User user);
}