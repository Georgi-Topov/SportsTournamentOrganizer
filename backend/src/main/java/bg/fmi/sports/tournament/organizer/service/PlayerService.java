package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.entity.Player;
import bg.fmi.sports.tournament.organizer.entity.embedded.Audit;
import bg.fmi.sports.tournament.organizer.exception.PlayerAlreadyInTeamException;
import bg.fmi.sports.tournament.organizer.exception.PlayerDuplicationException;
import bg.fmi.sports.tournament.organizer.exception.PlayerNotFoundException;
import bg.fmi.sports.tournament.organizer.repository.MembershipRepository;
import bg.fmi.sports.tournament.organizer.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MembershipRepository membershipRepository;

    public PlayerService(PlayerRepository playerRepository, MembershipRepository membershipRepository) {
        this.playerRepository = playerRepository;
        this.membershipRepository = membershipRepository;
    }

    public Player createPlayer(Player player) {
        player.setAudit(new Audit());

        try {
            Player savedPlayer = playerRepository.save(player);
            Audit audit = savedPlayer.getAudit();
            audit.setLastModifiedDate(null);
            audit.setLastModifiedBy(null);
            savedPlayer.setAudit(audit);
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
    public Player partiallyUpdatePlayerById(Long id, Player player) {
        player.setId(id);

        return playerRepository.findById(id).map(fetchedPlayer -> {
            checkForDuplication(player, fetchedPlayer);

            Optional.ofNullable(player.getFirstName()).ifPresent(fetchedPlayer::setFirstName);
            Optional.ofNullable(player.getLastName()).ifPresent(fetchedPlayer::setLastName);
            Optional.ofNullable(player.getBirthdate()).ifPresent(fetchedPlayer::setBirthdate);
            Optional.ofNullable(player.getGender()).ifPresent(fetchedPlayer::setGender);
            Optional.ofNullable(player.getWeight()).ifPresent(fetchedPlayer::setWeight);

            return playerRepository.save(fetchedPlayer);
        }).orElseThrow(() -> new PlayerNotFoundException("Player can not be found in the database"));
    }

    public void deletePlayerById(Long id) {
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
