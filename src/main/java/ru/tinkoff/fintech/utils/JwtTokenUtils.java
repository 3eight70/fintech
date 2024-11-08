package ru.tinkoff.fintech.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.tinkoff.fintech.configuration.properties.AuthenticationProperties;
import ru.tinkoff.fintech.token.repository.AccessTokenRepository;
import ru.tinkoff.fintech.user.entity.User;

import javax.crypto.SecretKey;

@Component
public class JwtTokenUtils {
    private final AuthenticationProperties properties;
    private final AccessTokenRepository accessTokenRepository;

    private final byte[] secretKey;
    private final SecretKey key;

    public JwtTokenUtils(
            AuthenticationProperties properties,
            AccessTokenRepository accessTokenRepository
    ) {
        this.properties = properties;
        this.secretKey = Base64.getDecoder().decode(properties.getSecret());
        this.key = Keys.hmacShaKeyFor(secretKey);
        this.accessTokenRepository = accessTokenRepository;
    }

    public AccessTokenInternalDto generateToken(User user, Boolean rememberMeFlag) {
        var claims = new HashMap<String, Object>();
        claims.put("role", user.getRole());

        var tokenId = UUID.randomUUID().toString();
        var issuedDate = new Date();
        var expiredDate = rememberMeFlag ?
                new Date(issuedDate.getTime() + properties.getRememberMeExpiration().toMillis()) :
                new Date(issuedDate.getTime() + properties.getExpiration().toMillis());

        var accessToken = Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .claim("userLogin", user.getLogin())
                .id(tokenId)
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(key)
                .compact();

        return new AccessTokenInternalDto(
                UUID.fromString(tokenId),
                accessToken,
                expiredDate
        );
    }

    public UUID getIdFromToken(String token) {
        return UUID.fromString(getClaimsFromToken(token).getId());
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(getClaimsFromToken(token).getSubject());
    }

    public Boolean validateToken(String token) {
        var accessTokenOptional = accessTokenRepository.findAccessTokenByTokenId(getIdFromToken(token));

        if (accessTokenOptional.isPresent()) {
            var accessToken = accessTokenOptional.get();
            if (accessToken.getExpiryTime().isBefore(Instant.now())) {
                accessTokenRepository.delete(accessToken);
                return false;
            }

            return true;
        }

        return false;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
