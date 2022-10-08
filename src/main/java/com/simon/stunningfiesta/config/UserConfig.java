package com.simon.stunningfiesta.config;

import com.simon.stunningfiesta.model.Role;
import com.simon.stunningfiesta.model.User;
import com.simon.stunningfiesta.service.IUserService;
import java.util.ArrayList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    CommandLineRunner runner(IUserService userService) {
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            userService.saveUser(new User(null, "Simon Si", "simon", "123456", new ArrayList<>()));
            userService.saveUser(new User(null, "Will Simth", "will", "123456", new ArrayList<>()));
            userService.saveUser(new User(null, "Jim Carry", "jim", "123456", new ArrayList<>()));
            userService.saveUser(new User(null, "Amy Zuo", "amy", "123456", new ArrayList<>()));

            userService.addRoleToUser("simon", "ROLE_USER");
            userService.addRoleToUser("simon", "ROLE_MANAGER");
            userService.addRoleToUser("will", "ROLE_MANAGER");
            userService.addRoleToUser("jim", "ROLE_ADMIN");
            userService.addRoleToUser("amy", "ROLE_SUPER_ADMIN");
            userService.addRoleToUser("amy", "ROLE_ADMIN");
            userService.addRoleToUser("amy", "ROLE_USER");
        };
    }
}
