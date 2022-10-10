package com.simon.stunningfiesta.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String privateKey;
    private String publicKey;
}
