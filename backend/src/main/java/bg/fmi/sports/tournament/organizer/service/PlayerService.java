package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.PlayerDuplicationException;
import bg.fmi.sports.tournament.organizer.exception.PlayerNotFoundException;
import bg.fmi.sports.tournament.organizer.exception.UserNotAuthorizedException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import bg.fmi.sports.tournament.organizer.repository.TeamRepository;
import bg.fmi.sports.tournament.organizer.vo.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final UserService userService;
    private final PlayerRepository playerRepository;
    private final MembershipRepository membershipRepository;
    private final TeamRepository teamRepository;

    public PlayerService(UserService userService,
                         PlayerRepository playerRepository, MembershipRepository membershipRepository,
                         TeamRepository teamRepository) {
        this.userService = userService;
        this.playerRepository = playerRepository;
        this.membershipRepository = membershipRepository;
        this.teamRepository = teamRepository;
    }

    public Player createPlayer(Player player, HttpServletRequest request) {
        User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);

        if (currentUser.getRole() != Role.MANAGER) {
            throw new UserNotAuthorizedException("Only users with manager role can create players");
        }

        player.setAudit(Audit.builder().build());

        try {
            Player savedPlayer = playerRepository.save(player);
            savedPlayer.setAudit(Audit.builder()
                .createdDate(savedPlayer.getAudit().getCreatedDate())
                .createdBy(savedPlayer.getAudit().getCreatedBy())
                .lastModifiedDate(null)
                .lastModifiedBy(null)
                .build());
            return savedPlayer;
        } catch (DataIntegrityViolationException ex) {
            throw new PlayerDuplicationException(
                "Player with the provided first name, last name and birthdate already exists", ex.getCause());
        }
    }

    public Page<Player> findAllPlayers(Pageable pageable) {
        return playerRepository.findAll(pageable);
    }

    public Optional<Player> findPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    @Transactional
    public Player partiallyUpdatePlayerById(Long id, Player player, HttpServletRequest request) {
        player.setId(id);

        User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);
        Optional<Long> latestTeamId = membershipRepository.findLatestTeamForPlayer(player.getId());

        if (latestTeamId.isPresent()) {
            Optional<Long> ownerId =
                teamRepository.findById(latestTeamId.get()).map(team -> team.getAudit().getCreatedBy());
            if (ownerId.isPresent() && !ownerId.get().equals(currentUser.getId())) {
                throw new UserNotAuthorizedException("Only the manager of the last team for the player " +
                    "can update a player");
            }
        } else {
            if (currentUser.getRole() != Role.MANAGER) {
                throw new UserNotAuthorizedException("Only managers can update a player");
            }
        }

        return playerRepository.findById(id).map(fetchedPlayer -> {
            checkForDuplication(player, fetchedPlayer);

            Optional.ofNullable(player.getFirstName()).ifPresent(fetchedPlayer::setFirstName);
            Optional.ofNullable(player.getLastName()).ifPresent(fetchedPlayer::setLastName);
            Optional.ofNullable(player.getBirthdate()).ifPresent(fetchedPlayer::setBirthdate);
            Optional.ofNullable(player.getGender()).ifPresent(fetchedPlayer::setGender);
            Optional.ofNullable(player.getWeight()).ifPresent(fetchedPlayer::setWeight);

            return playerRepository.save(fetchedPlayer);
        }).orElseThrow(() -> new PlayerNotFoundException("There is no player with the provided id"));
    }

    public void deletePlayerById(Long id, HttpServletRequest request) {
        User currentUser = userService.getUserFromTokenInAuthorizationHeader(request);
        if (currentUser.getRole() != Role.MANAGER) {
            throw new UserNotAuthorizedException("Only users with manager role can delete players");
        }

        if (isPlayerAssigned(id)) {
            throw new PlayerAlreadyInTeamException("The player is assigned to a team");
        }

        playerRepository.deleteById(id);
    }

    private boolean isPlayerAssigned(Long id) {
        return !membershipRepository.findByPlayerId(id).isEmpty();
    }

    private void checkForDuplication(Player givenPlayer, Player playerToUpdate) {
        if (givenPlayer.getFirstName() == null && givenPlayer.getLastName() == null
            && givenPlayer.getBirthdate() == null) {
            return;
        }

        Player playerToCheckForDuplication = Player.builder()
            .firstName(Optional.ofNullable(givenPlayer.getFirstName()).orElse(playerToUpdate.getFirstName()))
            .lastName(Optional.ofNullable(givenPlayer.getLastName()).orElse(playerToUpdate.getLastName()))
            .birthdate(Optional.ofNullable(givenPlayer.getBirthdate()).orElse(playerToUpdate.getBirthdate()))
            .build();

        if (playerRepository
            .findByFirstNameAndLastNameAndBirthdate(playerToCheckForDuplication.getFirstName(),
                playerToCheckForDuplication.getLastName(), playerToCheckForDuplication.getBirthdate())
            .isPresent()) {
            throw new PlayerDuplicationException("Player with the provided first name, " +
                "last name and birthdate already exists");
        }
    }

}
