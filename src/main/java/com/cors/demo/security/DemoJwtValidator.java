package com.cors.demo.security;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Custom token validation to verify our token provided by our Identity provider
 */
class DemoJwtValidator implements OAuth2TokenValidator<Jwt> {
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        /*
         We need to make a call to an Identity provider to verify
         token and active session
         */
        return OAuth2TokenValidatorResult.success();
    }
}