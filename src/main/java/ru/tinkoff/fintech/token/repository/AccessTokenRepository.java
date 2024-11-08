package ru.tinkoff.fintech.token.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tinkoff.fintech.token.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, UUID> {
    Optional<AccessToken> findAccessTokenByTokenId(UUID tokenId);

    void deleteAllByUserId(UUID userId);
}
