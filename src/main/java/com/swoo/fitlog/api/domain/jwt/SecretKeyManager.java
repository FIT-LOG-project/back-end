package com.swoo.fitlog.api.domain.jwt;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class SecretKeyManager {

    @Value("${custom.jwt.secretKey}")
    private String secretKeyPlain;
    private SecretKey encodedSecretKey;

    @PostConstruct
    public void init() {
        encodedSecretKey = createSecretKey();
    }

    public SecretKey getSecretKey() {
        return encodedSecretKey;
    }

    private SecretKey createSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }
}
