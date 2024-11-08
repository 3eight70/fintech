package ru.tinkoff.fintech.utils;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenInternalDto {
    private UUID tokenId;
    private String accessToken;
    private Date expiryTime;
}
