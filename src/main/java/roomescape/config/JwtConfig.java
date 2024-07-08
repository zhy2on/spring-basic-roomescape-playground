package roomescape.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.auth.JwtUtils;

@Configuration
public class JwtConfig {

    @Value("${roomescape.auth.jwt.secret}")
    private String secretKey;

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(secretKey);
    }
}
