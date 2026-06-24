package com.vttish.bookstore.auth.config;

import com.vttish.bookstore.auth.filter.JwtAuthenticationFilter;
import com.vttish.bookstore.auth.repository.UserRepository;
import com.vttish.bookstore.auth.service.JwtService;
import com.vttish.bookstore.common.config.FeatureProperties;
import com.vttish.bookstore.common.constant.ApiRoutingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthProperties authProps;

    @Value("${spring.h2.console.path:/h2-console}")
    private String h2ConsolePath;

    @Value("${springdoc.api-docs.path:/v3/api-docs}")
    private String apiDocsPath;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            UserRepository userRepository,
            FeatureProperties featureProps
    ) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        if (authProps.cors().enabled()) {
            http.cors(Customizer.withDefaults());
        } else {
            http.cors(AbstractHttpConfigurer::disable);
        }

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(ApiRoutingConstants.API_V1 + "/auth/**").permitAll();

            if (featureProps.enableDevTools()) {
                auth.requestMatchers(
                        apiDocsPath + "/**",
                        h2ConsolePath + "/**",
                        "/swagger-ui/**"
                ).permitAll();
            }

            auth.anyRequest().authenticated();
        }).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).addFilterBefore(
                new JwtAuthenticationFilter(jwtService, userRepository),
                UsernamePasswordAuthenticationFilter.class
        );

        if (featureProps.enableDevTools()) {
            http.headers(headers ->
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            );
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(authProps.cors().allowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
