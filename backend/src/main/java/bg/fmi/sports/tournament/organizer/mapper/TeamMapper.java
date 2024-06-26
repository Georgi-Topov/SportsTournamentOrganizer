package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.TeamDto;
import bg.fmi.sports.tournament.organizer.dto.TeamPartialResponseDto;
import bg.fmi.sports.tournament.organizer.entity.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SportTypeMapper.class)
public interface TeamMapper {

    Team dtoToTeam(TeamDto teamDto);

    TeamDto teamToDto(Team team);

    TeamPartialResponseDto teamToPartialResponseDto(Team team);

}
