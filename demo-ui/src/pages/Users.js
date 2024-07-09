// src/pages/Users.js

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import KeycloakService from '../keycloak'; // Import KeycloakService for Keycloak initialization

const Users = () => {
    const [keycloak, setKeycloak] = useState(null);
    const [userInfo, setUserInfo] = useState(null);

    useEffect(() => {
        const initializeKeycloak = async () => {
            const keycloakService = new KeycloakService();
            const keycloakInstance = await keycloakService.init();

            if (keycloakInstance) {
                setKeycloak(keycloakInstance);
            } else {
                console.error('Failed to initialize Keycloak');
            }
        };

        initializeKeycloak();
    }, []);

    useEffect(() => {
        if (keycloak) {
            const fetchData = async () => {
                try {
                    const response = await axios.get(`${window.env.baseURL}/users`, {
                        headers: {
                            Authorization: `Bearer ${keycloak.token}`,
                        },
                    });
                    setUserInfo(response.data);
                } catch (error) {
                    console.error('Failed to fetch user data:', error);
                }
            };

            fetchData();
        }
    }, [keycloak]);

    if (!keycloak || !userInfo) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h1>User Details</h1>
            <p>Username: {userInfo.username}</p>
            <p>Full Name: {userInfo.fullName}</p>
        </div>
    );
};

export default Users;
