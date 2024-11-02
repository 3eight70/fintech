package ru.tinkoff.fintech.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenInternalDto {
    private UUID tokenId;
    private String accessToken;
    private Date expiryTime;
}
