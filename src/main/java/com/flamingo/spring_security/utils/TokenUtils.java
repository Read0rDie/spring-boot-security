package com.flamingo.spring_security.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import com.flamingo.spring_security.models.User;

@Component
public class TokenUtils {

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
