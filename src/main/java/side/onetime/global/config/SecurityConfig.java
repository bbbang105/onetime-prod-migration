package side.onetime.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import side.onetime.auth.handler.OAuthLoginFailureHandler;
import side.onetime.auth.handler.OAuthLoginSuccessHandler;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;

    private static final String[] SWAGGER_URLS = {
            "/swagger-ui/**", "/v3/api-docs/**"
    };

    private static final String[] PUBLIC_URLS = {
            "/api/v1/events/**",
            "/api/v1/schedules/**",
            "/api/v1/members/**",
            "/api/v1/urls/**",
            "/api/v1/tokens/**",
            "/api/v1/users/onboarding"
    };

    private static final String[] AUTHENTICATED_URLS = {
            "/api/v1/users/**",
            "/api/v1/fixed-schedules/**",
    };

    private static final String[] ALLOWED_ORIGINS = {
            "http://localhost:5173",
            "https://onetime-test.vercel.app",
            "https://www.onetime-test.vercel.app",
            "https://onetime-with-members.com",
            "https://www.onetime-with-members.com",
            "https://1-ti.me",
            "https://onetime-test.store",
    };

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(ALLOWED_ORIGINS));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Set-Cookie"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SWAGGER_URLS).permitAll()
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(AUTHENTICATED_URLS).authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuthLoginSuccessHandler)
                        .failureHandler(oAuthLoginFailureHandler)
                );

        return httpSecurity.build();
    }
}
