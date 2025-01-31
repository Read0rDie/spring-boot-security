package com.flamingo.spring_security.utils;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import com.flamingo.spring_security.models.User;

@Component
public class TokenUtils {

    private final long ONE_MINUTE = 60;
    private final long FIVE_MINUTES = ONE_MINUTE * 5;

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    public String getAccessToken() {
        OAuth2AuthorizedClient client = getClient();
        if (client != null && client.getAccessToken() != null) {
            return client.getAccessToken().getTokenValue();
        }
        return null;
    }

    public String getRefreshToken() {
        OAuth2AuthorizedClient client = getClient();
        return client.getRefreshToken().getTokenValue();
    }

    public OAuth2AuthorizedClient getClient() {
        OAuth2AuthenticationToken oauthToken = getOAuthToken();
        return clientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public void refreshAccessToken(String principalName) {
        OAuth2AuthorizedClient authorizedClient = getClient();
        OAuth2RefreshTokenGrantRequest grantRequest = new OAuth2RefreshTokenGrantRequest(
                authorizedClient.getClientRegistration(),
                authorizedClient.getAccessToken(),
                authorizedClient.getRefreshToken());
        RestClientRefreshTokenTokenResponseClient tokenResponseClient = new RestClientRefreshTokenTokenResponseClient();
        OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(grantRequest);
        OAuth2AuthorizedClient updatedAuthorizedClient = new OAuth2AuthorizedClient(
                authorizedClient.getClientRegistration(),
                authorizedClient.getPrincipalName(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken());
        clientService.saveAuthorizedClient(updatedAuthorizedClient, getAuthentication());
    }

    public boolean isTokenValid(OAuth2AccessToken accessToken) {
        if (accessToken != null) {
            return accessToken.getExpiresAt().minusSeconds(FIVE_MINUTES).compareTo(Instant.now()) > 0;
        }
        return false;
    }

    public User getUser() {
        var principal = getAuthentication().getPrincipal();
        if (principal == null || !principal.getClass().isAssignableFrom(DefaultOidcUser.class)) {
            return null;
        }
        OidcUser user = (DefaultOidcUser) principal;
        User authenticatedUser = new User();
        authenticatedUser.setEmail(user.getAttribute("email"));
        authenticatedUser.setFirstName(user.getAttribute("given_name"));
        authenticatedUser.setLastName(user.getAttribute("family_name"));
        authenticatedUser.setAvatarURL(user.getAttribute("picture"));
        return authenticatedUser;
    }

    private OAuth2AuthenticationToken getOAuthToken() {
        Authentication authentication = getAuthentication();
        if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
            return (OAuth2AuthenticationToken) authentication;
        }
        throw new SecurityException("Authentication is not OAuth2AuthenticationToken");
    }

}
