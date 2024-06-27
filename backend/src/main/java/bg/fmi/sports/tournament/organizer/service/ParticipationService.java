package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Participation;
import bg.fmi.sports.tournament.organizer.entity.Team;
import bg.fmi.sports.tournament.organizer.entity.Tournament;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.entity.embedded.ParticipationId;
import bg.fmi.sports.tournament.organizer.exception.TeamAlreadyInTournamentException;
import bg.fmi.sports.tournament.organizer.exception.TeamNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TeamToTournamentBadCorrespondenceException;
import bg.fmi.sports.tournament.organizer.exception.TournamentNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.TournamentOverException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.ParticipationRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service("participation")
public class ParticipationService extends AffiliationService {

    private final MembershipRepository membershipRepository;
    private final TeamRepository teamRepository;

    public ParticipationService(ParticipationRepository participationRepository,
                                MembershipRepository membershipRepository,
                                TeamRepository teamRepository, TournamentRepository tournamentRepository) {
        super(participationRepository, tournamentRepository);
        this.membershipRepository = membershipRepository;
        this.teamRepository = teamRepository;
    }

    public void addTeamToTournament(Tournament tournament, Team team) {
        ParticipationId participationId = new ParticipationId(tournament.getId(), team.getId());

        Participation participation = Participation.builder()
            .id(participationId)
            .tournament(tournament)
            .team(team)
            .audit(new Audit())
            .build();

        if (participationRepository.existsById(new ParticipationId(tournament.getId(), team.getId()))) {
            throw new TeamAlreadyInTournamentException("The team is already registered to the tournament");
        }

        participationRepository.save(participation);
    }

    public void registerTeamToTournament(Long tournamentId, Long teamId) {
        Optional<Tournament> tournament = tournamentRepository.findById(tournamentId);
        if (tournament.isEmpty()) {
            throw new TournamentNotFoundException("Tournament with an id of " + tournamentId
                + " is not present in the database");
        }

        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) {
            throw new TeamNotFoundException("Team with an id of " + teamId + " is not present in the database");
        }

        Tournament fetchedTournament = tournament.get();
        Team fetchedTeam = team.get();

        checkIfTournamentHasStarted(fetchedTournament);

        Tournament latestTournamentOfTeam = checkTeamParticipationStatus(teamId).orElse(null);
        checkTeamCorrespondenceToTournament(fetchedTournament, fetchedTeam, latestTournamentOfTeam);

        addTeamToTournament(fetchedTournament, fetchedTeam);
    }

    public void checkIfTournamentHasStarted(Tournament fetchedTournament) {
        if (LocalDateTime.now().isAfter(fetchedTournament.getStartDate())) {
            throw new TournamentOverException("Cannot register the team to a tournament which has already started");
        }
    }

    public void checkTeamCorrespondenceToTournament(Tournament tournament, Team team,
                                                    Tournament latestTournament) {
        if (tournament.getSportType() != team.getSportType()) {
            throw new TeamToTournamentBadCorrespondenceException(
                "The sport type of the team does not match the sport type of the tournament which is "
                    + tournament.getSportType()
            );
        }

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
                    + " to participate in the tournament. Now it has " + numberOfPlayersInTeamForTournament
            );
        }

        // todo(maybe) : check if the number of players in the team <= maxPlayersPerTeam
    }


    public Set<Participation> findAllParticipatingTeams(Long tournamentId) {
        return participationRepository.findByTournamentId(tournamentId);
    }
}
