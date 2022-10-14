package com.simon.stunningfiesta.config;

import com.simon.stunningfiesta.model.Role;
import com.simon.stunningfiesta.model.RoleEnum;
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
            userService.saveRole(new Role(null, RoleEnum.USER.getName()));
            userService.saveRole(new Role(null, RoleEnum.MANAGER.getName()));
            userService.saveRole(new Role(null, RoleEnum.ADMIN.getName()));

            userService.saveUser(new User(null, "Simon Si", "simon", "123456", new ArrayList<>()));
            userService.saveUser(new User(null, "Will Simth", "will", "123456", new ArrayList<>()));
            userService.saveUser(new User(null, "Jim Carry", "jim", "123456", new ArrayList<>()));
            userService.saveUser(new User(null, "Amy Zuo", "amy", "123456", new ArrayList<>()));

            userService.addRoleToUser("simon", RoleEnum.USER.getName());
            userService.addRoleToUser("simon", RoleEnum.ADMIN.getName());
            userService.addRoleToUser("will", RoleEnum.USER.getName());
            userService.addRoleToUser("jim", RoleEnum.MANAGER.getName());
            userService.addRoleToUser("amy", RoleEnum.ADMIN.getName());
        };
    }
}
