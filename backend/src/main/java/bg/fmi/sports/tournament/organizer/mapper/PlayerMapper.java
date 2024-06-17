package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.PlayerDto;
import bg.fmi.sports.tournament.organizer.entity.Player;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    Player dtoToPlayer(PlayerDto playerDto);

    PlayerDto playerToDto(Player player);

}
