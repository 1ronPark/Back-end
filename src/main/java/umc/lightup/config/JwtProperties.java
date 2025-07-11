package umc.lightup.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("jwt.token")
public class JwtProperties {
    private String secretKey;
    private Expiration expiration;

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    @Getter
    @Setter
    public static class Expiration{
        private Long access;
        // TODO: refreshToken
    }
}