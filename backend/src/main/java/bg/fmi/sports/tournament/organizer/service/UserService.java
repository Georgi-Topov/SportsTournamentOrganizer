package bg.fmi.sports.tournament.organizer.service;

import bg.fmi.sports.tournament.organizer.config.JwtService;
import bg.fmi.sports.tournament.organizer.dto.UserDto;
import bg.fmi.sports.tournament.organizer.entity.User;
import bg.fmi.sports.tournament.organizer.exception.UserNotFoundException;
import bg.fmi.sports.tournament.organizer.mapper.UserMapper;
import bg.fmi.sports.tournament.organizer.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserFromTokenInAuthorizationHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        String username = new JwtService().extractUsername(authHeader.substring(7));

        return getUserByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("There is no user with the provided username"));
    }

    public UserDto registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var token = jwtService.generateToken(user);
        userRepository.save(user);
        UserDto res = userMapper.userToDto(user);
        res.setToken(token);
        return res;
    }

    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    public User partialUpdate(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            Optional.ofNullable(user.getEmail()).ifPresent(existingUser::setEmail);
            Optional.ofNullable(user.getUsername()).ifPresent(existingUser::setUsername);
            Optional.ofNullable(user.getPassword()).ifPresent(pass ->
                    existingUser.setPassword(passwordEncoder.encode(pass)));
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new UserNotFoundException("User does not exist"));
    }

    public UserDto authUser(User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        UserDto res = userMapper.userToDto(userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User does not exist")));
        String token = jwtService.generateToken(user);
        res.setToken(token);
        return res;
    }
}
