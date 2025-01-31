package com.flamingo.spring_security.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.flamingo.spring_security.models.User;

@Service
public class UserRepository implements Repository<User> {

    // for the purposes fo this demo this HashMap will behave as a mock DB
    // repository
    private Map<String, User> userRepository = new HashMap<String, User>();

    @Override
    public User find(String id) {
        return this.userRepository.getOrDefault(id, null);
    }

    @Override
    public User save(User newUser) {
        String id = UUID.randomUUID().toString();
        newUser.setId(id);
        this.userRepository.put(id, newUser);
        return newUser;
    }

    @Override
    public User delete(User newUser) {
        return this.userRepository.put(newUser.getId(), null);
    }

    public User getByEmail(String email) {
        List<User> filteredUsers = userRepository.entrySet().stream()
                .filter(entry -> entry.getValue().getEmail().equalsIgnoreCase(email))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
        if (filteredUsers.size() == 0) {
            return null;
        } else if (filteredUsers.size() == 1) {
            return filteredUsers.get(0);
        } else {
            throw new IllegalStateException(
                    "ERROR : UserRepository.getByEmail() - Multiple user profiles were found when a maximum of one profile was expected.");
        }
    }

}
