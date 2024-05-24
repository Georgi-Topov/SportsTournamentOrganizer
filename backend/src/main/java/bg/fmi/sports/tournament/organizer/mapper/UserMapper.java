package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.UserDto;
import bg.fmi.sports.tournament.organizer.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User dtoToUser(UserDto userDto);

    UserDto userToDto(User user);
}