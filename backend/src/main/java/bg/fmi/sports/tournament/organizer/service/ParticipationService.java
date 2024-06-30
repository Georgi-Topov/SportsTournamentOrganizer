package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamToTournamentBadCorrespondenceException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentOverException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service("participation")
public class ParticipationService extends AffiliationService {

    private final MembershipRepository membershipRepository;

    public ParticipationService(UserService userService, ParticipationRepository participationRepository,
                                MembershipRepository membershipRepository,
                                TeamRepository teamRepository, TournamentRepository tournamentRepository) {
        super(userService, participationRepository, tournamentRepository, teamRepository);
        this.membershipRepository = membershipRepository;
    }

    private Participation addTeamToTournament(Tournament tournament, Team team) {
        ParticipationId participationId = new ParticipationId(tournament.getId(), team.getId());

        Participation participation = Participation.builder()
            .id(participationId)
            .tournament(tournament)
            .team(team)
            .audit(Audit.builder().build())
            .build();

        if (participationRepository.existsById(new ParticipationId(tournament.getId(), team.getId()))) {
            throw new TeamAlreadyInTournamentException("The team is already registered for the tournament");
        }

        return participationRepository.save(participation);
    }

    public Participation registerTeamToTournament(Long tournamentId, Long teamId, HttpServletRequest request) {
        Tournament fetchedTournament = checkTournamentPresenceInTheDatabase(tournamentId);
        Team fetchedTeam = checkTeamPresenceInTheDatabase(teamId);

        validateTeamOwnership(fetchedTeam, request);

        checkIfTournamentHasStarted(fetchedTournament);

        Tournament latestTournamentOfTeam = checkTeamParticipationStatus(teamId).orElse(null);
        checkTeamCorrespondenceToTournament(fetchedTournament, fetchedTeam, latestTournamentOfTeam);

        return addTeamToTournament(fetchedTournament, fetchedTeam);
    }

    private Tournament checkTournamentPresenceInTheDatabase(Long tournamentId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);

        if (tournament.isEmpty()) {
            throw new TournamentNotFoundException("There is no tournament with an id " + tournamentId);
        } else {
            return tournament.get();
        }
    }

    private void checkIfTournamentHasStarted(Tournament fetchedTournament) {
        if (LocalDateTime.now().isAfter(fetchedTournament.getStartDate())) {
            throw new TournamentOverException("Cannot register the team for a tournament which has already started");
        }
    }

    private void checkTeamCorrespondenceToTournament(Tournament tournament, Team team,
                                                    Tournament latestTournament) {

//        if (!tournament.getSportType().equals(team.getSportType())) {
//            throw new TeamToTournamentBadCorrespondenceException(
//                "The sport type of the team does not match the sport type of the tournament which is "
//                    + tournament.getSportType().getSportType()
//            );
//        }

        Integer numberOfPlayersInTeamForTournament;
        if (latestTournament != null) {
            numberOfPlayersInTeamForTournament = membershipRepository
                .findMemberPlayersFromMembershipCreatedDateAfterLastTournament(
                    latestTournament.getEndDate(), team.getId()).size();
        } else {
            numberOfPlayersInTeamForTournament =
                membershipRepository.findMemberPlayersFromMembershipCreatedDate(team.getId()).size();
        }

        if (tournament.getMinimumPlayersPerTeam() > numberOfPlayersInTeamForTournament) {
            throw new TeamToTournamentBadCorrespondenceException(
                "Team needs to have at least " + tournament.getMinimumPlayersPerTeam()
                    + " players to participate in the tournament. Now it has " + numberOfPlayersInTeamForTournament
            );
        }

    }


    public Set<Participation> findAllParticipatingTeams(Long tournamentId) {
        checkTournamentPresenceInTheDatabase(tournamentId);

        return participationRepository.findByTournamentId(tournamentId);
    }
}
