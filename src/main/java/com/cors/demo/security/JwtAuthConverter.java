package com.cors.demo.security;


import com.cors.demo.user.User;
import com.cors.demo.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class convertor to assigned roles to the user based on the token
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();


    private UserService userService;

    public JwtAuthConverter(UserService userService) {
        this.userService = userService;
    }

    private final String principleAttribute = "preferred_username";

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String cleintId;

    /**
     * Creation of the user in our DB in case it is not there
     * @param jwt
     * @return
     */
    private Mono<User> createUser(Jwt jwt) {
        User user = new User();
        user.setUsername(jwt.getClaim("preferred_username"));
        user.setKcUuid(jwt.getClaim("sub"));

        return userService.addUser(user).thenReturn(user);
    }

    /**
     * Check the user's token and check for the user in our DB
     * @param jwt
     * @return
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(@NonNull Jwt jwt) {
        String username = jwt.getClaim("preferred_username");

        return userService.findByUsername(username)
                .switchIfEmpty(createUser(jwt))
                .then(createAuthenticationToken(jwt));
    }

    /**
     * Creation of the token for the authenticated user
     * @param jwt
     * @return
     */
    private Mono<AbstractAuthenticationToken> createAuthenticationToken(Jwt jwt) {
        return Mono.fromSupplier(() -> {
            Collection<GrantedAuthority> authorities = Stream.concat(
                    jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                    extractRealmRoles(jwt).stream()
            ).collect(Collectors.toSet());

            return new JwtAuthenticationToken(
                    jwt,
                    authorities,
                    getPrincipleClaimName(jwt)
            );
        });
    }


    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }

    /**
     * Extracting roles from the authentication token
     * @param jwt Token coming from Keycloak
     * @return Spring Security roles (in Controller can be extracted as 'hasRole')
     */
    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        String realm_access = "realm_access";  // Realm roles
        String resource_access = "resource_access";  // Client roles

        Map<String, Object> resourceAccess;
        Map<String, Object> clientAccess;
        Map<String, Object> clientResource;

        Collection<String> resourceRoles = new ArrayList<>();
        List<String> clientRoles = new ArrayList<>();
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Extracting Realm roles
        if (jwt.getClaim(realm_access) != null) {

            resourceAccess = jwt.getClaim(realm_access);
            resourceRoles = (Collection<String>) resourceAccess.get("roles");

            // Convert resourceRoles to SimpleGrantedAuthority with "ROLE_" prefix
            authorities.addAll(resourceRoles
                    .stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet()));
        }
        // Extracting Client roles
        if (jwt.getClaim(resource_access) != null) {
            clientAccess = jwt.getClaim(resource_access);
            if (clientAccess.get(cleintId) != null) {
                clientResource = (Map<String, Object>) clientAccess.get(cleintId);

                clientRoles = (List<String>) clientResource.get("roles");

                authorities.addAll(clientRoles
                        .stream()
                        .map(permissions -> new SimpleGrantedAuthority(permissions))
                        .collect(Collectors.toSet()));
            }
        }

        return authorities;
    }
}
