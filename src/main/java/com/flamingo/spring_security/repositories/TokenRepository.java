package com.flamingo.spring_security.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.flamingo.spring_security.models.OAuthToken;

@Service
public class TokenRepository implements Repository<OAuthToken> {

    // for the purposes fo this demo this HashMap will behave as a mock DB
    // repository
    private Map<String, OAuthToken> tokenRepository = new HashMap<String, OAuthToken>();

    @Override
    public OAuthToken find(String id) {
        return this.tokenRepository.getOrDefault(id, null);
    }

    @Override
    public OAuthToken save(OAuthToken newToken) {
        return this.tokenRepository.put(newToken.getAccessToken(), newToken);
    }

    @Override
    public OAuthToken delete(OAuthToken token) {
        return this.tokenRepository.put(token.getAccessToken(), null);
    }

}
