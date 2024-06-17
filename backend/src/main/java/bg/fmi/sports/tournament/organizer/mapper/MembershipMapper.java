package bg.fmi.sports.tournament.organizer.mapper;

import bg.fmi.sports.tournament.organizer.dto.MembershipDto;
import bg.fmi.sports.tournament.organizer.entity.Membership;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MembershipMapper {

    Membership dtoToMembership(MembershipDto membershipDto);

    MembershipDto membershipToDto(Membership membership);

}
