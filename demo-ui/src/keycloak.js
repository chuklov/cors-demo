
import Keycloak from 'keycloak-js';

class KeycloakService {
    constructor() {
        this.keycloak = null;
    }

    async init() {
        if (!this.keycloak) {
            // Initialize Keycloak instance
            this.keycloak = new Keycloak({
                url: window.env.keycloak.url,
                realm: window.env.keycloak.realm,
                clientId: window.env.keycloak.clientId
            });

            try {
                // Initialize Keycloak
                const authenticated = await this.keycloak.init({});
                if (authenticated) {
                    console.log('Keycloak initialized successfully');
                } else {
                    console.error('Authentication failed');
                }
            } catch (error) {
                console.error('Keycloak initialization error:', error);
            }
        }
        return this.keycloak;
    }

    getKeycloakInstance() {
        return this.keycloak;
    }
}

const keycloakService = new KeycloakService();
export default keycloakService;
