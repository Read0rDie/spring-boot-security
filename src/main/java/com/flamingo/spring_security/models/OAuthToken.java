package com.flamingo.spring_security.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthToken {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime expirationTime;
    private User user;

}
