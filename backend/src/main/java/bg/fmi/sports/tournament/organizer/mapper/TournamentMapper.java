package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.TournamentDto;
import bg.fmi.sports.tournament.organizer.dto.TournamentPartialResponseDto;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = SportTypeMapper.class)
public interface TournamentMapper {

    Tournament dtoToTournament(TournamentDto tournamentDto);

    TournamentDto tournamentToDto(Tournament tournament);

    TournamentPartialResponseDto tournamentToPartialResponseDto(Tournament tournament);

}
