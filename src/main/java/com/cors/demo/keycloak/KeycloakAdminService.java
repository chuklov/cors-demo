package com.cors.demo.keycloak;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Keycloak service to fetch or update user's details
 */
@Service
public class KeycloakAdminService {

    private static final Logger LOGGER = LogManager.getLogger(KeycloakAdminService.class);

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client}")
    private String clientId;

    @Value("${keycloak.secret}")
    private String clientSecret;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        LOGGER.info("initialization");
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
            LOGGER.info("Initialized Keycloak client successfully");
        } catch (Exception e) {
            LOGGER.info( "Failed to initialize Keycloak client", e);
        }
    }

    private Keycloak getKeycloakInstance() {
        if (keycloak == null) {
            init();
        }
        return keycloak;
    }

    /**
     * Get user's details based on the username
     * @param username
     * @return
     */
    public UserRepresentation getUserByUsername(String username) {
        LOGGER.info("Getting user details from kc by username: " + username);
        try {
            LOGGER.info("Fetching user with username: " + username);
            UsersResource usersResource = getKeycloakInstance().realm(realm).users();
            List<UserRepresentation> users = usersResource.search(username, true);
            if (users != null && !users.isEmpty()) {
                LOGGER.info("User found: " + users.get(0).getUsername());
                return users.get(0);
            } else {
                LOGGER.info("User not found: " + username);
            }
        } catch (Exception e) {
            LOGGER.info("Failed to fetch user by username: ", e);
        }
        return null;
    }
}
