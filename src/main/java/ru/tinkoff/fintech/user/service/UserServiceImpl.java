package ru.tinkoff.fintech.user.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tinkoff.fintech.common.dto.Response;
import ru.tinkoff.fintech.token.dto.AccessTokenDto;
import ru.tinkoff.fintech.token.entity.AccessToken;
import ru.tinkoff.fintech.token.repository.AccessTokenRepository;
import ru.tinkoff.fintech.user.dto.ChangePasswordDto;
import ru.tinkoff.fintech.user.dto.LoginCredentials;
import ru.tinkoff.fintech.user.dto.RegisterUserDto;
import ru.tinkoff.fintech.user.dto.UserDto;
import ru.tinkoff.fintech.user.entity.User;
import ru.tinkoff.fintech.user.exceptions.UnauthorizedException;
import ru.tinkoff.fintech.user.exceptions.UserAlreadyExistsException;
import ru.tinkoff.fintech.user.exceptions.UserNotFoundException;
import ru.tinkoff.fintech.user.exceptions.WrongPasswordException;
import ru.tinkoff.fintech.user.mapper.UserMapper;
import ru.tinkoff.fintech.user.repository.UserRepository;
import ru.tinkoff.fintech.utils.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final AccessTokenRepository accessTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    @Transactional
    public AccessTokenDto registerUser(RegisterUserDto registerUserDto) {
        registerUserDto.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));

        Optional<User> existingUser = userRepository.findByLogin(registerUserDto.getLogin());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException(registerUserDto.getLogin());
        }

        User user = UserMapper.registerDtoToEntity(registerUserDto);
        userRepository.save(user);

        var accessToken = jwtTokenUtils.generateToken(user, false);
        var tokenEntity = new AccessToken(
                UUID.randomUUID(),
                accessToken.getTokenId(),
                user.getId(),
                accessToken.getExpiryTime().toInstant()
        );
        accessTokenRepository.save(tokenEntity);

        return new AccessTokenDto(accessToken.getAccessToken());
    }

    @Override
    @Transactional
    public AccessTokenDto loginUser(LoginCredentials loginCredentials) {
        User user = userRepository.findByLogin(loginCredentials.getLogin())
                .orElseThrow(UnauthorizedException::new);

        var accessToken = jwtTokenUtils.generateToken(user, loginCredentials.getRememberMe());
        var tokenEntity = new AccessToken(
                UUID.randomUUID(),
                accessToken.getTokenId(),
                user.getId(),
                accessToken.getExpiryTime().toInstant()
        );
        accessTokenRepository.save(tokenEntity);

        return new AccessTokenDto(accessToken.getAccessToken());
    }

    @Override
    @Transactional
    public Response logoutUser(UserDto userDto) {
        accessTokenRepository.deleteAllByUserId(userDto.getId());

        return new Response(
                HttpStatus.OK.value(),
                "Пользователь успешно разлогинился",
                Instant.now()
        );
    }

    @Override
    @Transactional
    public Response changePassword(UserDto userDto, ChangePasswordDto changePasswordDto) {
        var user = userRepository.findByLogin(userDto.getLogin())
                .orElseThrow(() -> new UserNotFoundException(userDto.getLogin()));

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        changePasswordDto.setNewPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        user.setPassword(changePasswordDto.getNewPassword());
        userRepository.save(user);

        return new Response(
                HttpStatus.OK.value(),
                "Пароль успешно изменен",
                Instant.now()
        );
    }
}
