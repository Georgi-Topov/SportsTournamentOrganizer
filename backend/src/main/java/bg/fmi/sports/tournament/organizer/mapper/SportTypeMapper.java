package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.SportTypeDto;
import bg.fmi.sports.tournament.organizer.entity.SportType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SportTypeMapper {

    SportType dtoToSportType(SportTypeDto sportTypeDto);

    SportTypeDto sportTypeToDto(SportType sportType);

}
