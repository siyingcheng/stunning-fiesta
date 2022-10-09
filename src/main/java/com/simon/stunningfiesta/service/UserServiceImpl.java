package com.simon.stunningfiesta.service;

import com.simon.stunningfiesta.model.Role;
import com.simon.stunningfiesta.model.User;
import com.simon.stunningfiesta.repository.IRoleRepository;
import com.simon.stunningfiesta.repository.IUserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements IUserService, UserDetailsService {
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("User not found in the database, username: {}", username);
            throw new UsernameNotFoundException("User not exist");
        }
        log.info("User found in the database, username: {}", username);
        Collection<SimpleGrantedAuthority> authories = new ArrayList<>();
        user.getRoles().forEach(role -> authories.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authories);
    }

    @Override
    public User saveUser(User user) {
        log.info("Saving new user `{}` to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role `{}` to the database", role.getName());
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role `{}` to user `{}`", roleName, username);
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        log.info("Fetting user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetting all users");
        return userRepository.findAll();
    }
}
