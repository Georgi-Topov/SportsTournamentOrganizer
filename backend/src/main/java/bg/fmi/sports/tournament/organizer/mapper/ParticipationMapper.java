package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.ParticipationDto;
import bg.fmi.sports.tournament.organizer.dto.ParticipationPartialResponseDto;
import bg.fmi.sports.tournament.organizer.entity.Participation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    Participation dtoToParticipation(ParticipationDto participationDto);

    ParticipationDto participationToDto(Participation participation);

    ParticipationPartialResponseDto participationToPartialResponseDto(Participation participation);

}
