package com.flamingo.spring_security.models;

import lombok.Data;

@Data
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String avatarURL;
    private String email;
    private String password;
    private String role;

    public String getUsername() {
        return getEmail();
    }

}
