package com.cors.demo.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


import java.util.Arrays;


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager=true)
public class SecurityConfig {
    private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);

    private final JwtAuthConverter jwtAuthConverter;
    private final Environment env;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter, Environment env) {
        this.jwtAuthConverter = jwtAuthConverter;
        this.env = env;
    }


    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        LOGGER.info("<================   securityFilterChain =================>");
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/info/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth -> oAuth.jwt(jwt -> {
                    jwt.jwtDecoder(jwtDecoder());
                    jwt.jwtAuthenticationConverter(jwtAuthConverter);
                }))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        String allowed = "http://localhost:3000";

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowed));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));

        LOGGER.info(String.format("<================  GET ORIGINS: %s =================>",configuration.getAllowedOrigins()));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * This decoder is part of a new Spring security token validation
     * @return validated token
     */
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String keycloakUrl = env.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
        NimbusReactiveJwtDecoder jwtDecoder = (NimbusReactiveJwtDecoder) ReactiveJwtDecoders.fromIssuerLocation(keycloakUrl);
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(keycloakUrl), new DemoJwtValidator()));
        return jwtDecoder;
    }
}

