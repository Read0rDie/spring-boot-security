package com.flamingo.spring_security.helpers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.flamingo.spring_security.models.OAuthToken;
import com.flamingo.spring_security.models.User;
import com.flamingo.spring_security.repositories.TokenRepository;
import com.flamingo.spring_security.repositories.UserRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;

@Component
public class LoginHelper {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    public OAuthToken processGrantCode(String code) {
        String accessToken = getOauthAccessTokenGoogle(code);
        User googleUser = getProfileDetailsGoogle(accessToken);
        User user = userRepository.getByEmail(googleUser.getEmail());
        if (user == null) {
            user = userRepository.save(googleUser);
        }
        return saveTokenForUser(user);
    }

    private User getProfileDetailsGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        JsonObject jsonObject = new Gson().fromJson(response.getBody(), JsonObject.class);

        User user = new User();
        user.setEmail(jsonObject.get("email").toString().replace("\"", ""));
        user.setFirstName(jsonObject.get("given_name").toString().replace("\"", ""));
        user.setLastName(jsonObject.get("family_name").toString().replace("\"", ""));
        user.setPassword(UUID.randomUUID().toString());

        return user;
    }

    private String getOauthAccessTokenGoogle(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:8080/grantcode");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
        params.add("scope", "openid");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        String url = "https://oauth2.googleapis.com/token";
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

        return jsonObject.get("access_token").toString().replace("\"", "");
    }

    private OAuthToken saveTokenForUser(User user) {
        OAuthToken token = generateToken();
        token.setUser(user);
        tokenRepository.save(token);
        return token;
    }

    private OAuthToken generateToken() {
        OAuthToken token = new OAuthToken();
        token.setAccessToken(UUID.randomUUID().toString());
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setExpirationTime(LocalDateTime.now().plusHours(1));
        return token;
    }

}
