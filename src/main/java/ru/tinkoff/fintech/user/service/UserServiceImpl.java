package ru.tinkoff.fintech.user.service;

import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.token.dto.AccessTokenDto;
import ru.tinkoff.fintech.token.repository.AccessTokenRepository;
import ru.tinkoff.fintech.user.dto.ChangePasswordDto;
import ru.tinkoff.fintech.user.dto.LoginCredentials;
import ru.tinkoff.fintech.user.dto.RegisterUserDto;
import ru.tinkoff.fintech.user.dto.UserDto;
import ru.tinkoff.fintech.user.entity.User;
import ru.tinkoff.fintech.user.exceptions.UnauthorizedException;
import ru.tinkoff.fintech.user.exceptions.UserAlreadyExistsException;
import ru.tinkoff.fintech.user.exceptions.UserNotFoundException;
import ru.tinkoff.fintech.user.mapper.UserMapper;
import ru.tinkoff.fintech.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public AccessTokenDto registerUser(RegisterUserDto registerUserDto) {
        Optional<User> existingUser = userRepository.findByLogin(registerUserDto.getLogin());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException(registerUserDto.getLogin());
        }

        User user = UserMapper.registerDtoToEntity(registerUserDto);
        userRepository.save(user);

        return null;
    }

    @Override
    public AccessTokenDto loginUser(LoginCredentials loginCredentials) {
        User user = userRepository.findByLogin(loginCredentials.getLogin())
                .orElseThrow(UnauthorizedException::new);

        if (!user.getPassword().equals(loginCredentials.getPassword())){
            throw new UnauthorizedException();
        }

        return null;
    }

    @Override
    public Response logoutUser(UserDto userDto) {
        accessTokenRepository.deleteAllByUserId(userDto.getId());

        return new Response(
                HttpStatus.OK.value(),
                "Пользователь успешно разлогинился",
                Instant.now()
        );
    }

    @Override
    public Response changePassword(UserDto userDto, ChangePasswordDto changePasswordDto) {
        return null;
    }
}
