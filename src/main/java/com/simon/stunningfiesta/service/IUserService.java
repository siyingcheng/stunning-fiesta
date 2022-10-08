package com.simon.stunningfiesta.service;

import com.simon.stunningfiesta.model.Role;
import com.simon.stunningfiesta.model.User;
import java.util.List;

public interface IUserService {
    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    User getUser(String username);

    List<User> getUsers();
}
