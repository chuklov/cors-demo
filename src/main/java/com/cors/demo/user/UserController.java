package com.cors.demo.user;


import com.cors.demo.keycloak.KeycloakAdminService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    private UserService userService;
    private final KeycloakAdminService keycloakAdminService;

    public UserController(UserService userService, KeycloakAdminService keycloakAdminService) {
        this.userService = userService;
        this.keycloakAdminService = keycloakAdminService;
    }


    /**
     * Get all users in the DB
     * @return User
     */
    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.findAll();
    }

    /**
     * Get User by ID
     * @param id Accept ID as digits only
     * @return User
     */
    @GetMapping("/{id:\\d+}")
    public Mono<User> getUserById(@PathVariable int id) {
        LOGGER.debug("getUserById with id: " + id);
        return userService.findById(id);
    }

    /**
     * Get User by Username
     * @param username Accept username only as chars and underscores
     * @return User
     */
    @GetMapping("/{username:[a-zA-Z_]+}")
    public Mono<User> getUserByUsername(@PathVariable String username) {
        LOGGER.debug("getUserById with username: " + username);
        return userService.findByUsername(username);
    }

    @GetMapping(value = "/self", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        LOGGER.info("Authenticated user: " + username);

        return userService.findByUsername(username)
                .map(user -> {
                    UserRepresentation keycloakUser = keycloakAdminService.getUserByUsername(username);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("ID", user.getId());
                        json.put("username", user.getUsername());
                        if (keycloakUser != null) {
                            json.put("firstName", keycloakUser.getFirstName());
                            json.put("lastName", keycloakUser.getLastName());
                            json.put("email", keycloakUser.getEmail());
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    // Add other user fields as needed
                    return json.toString();
                });
    }

}
