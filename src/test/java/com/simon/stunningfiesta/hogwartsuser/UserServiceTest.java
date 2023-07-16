package com.simon.stunningfiesta.hogwartsuser;

import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private List<HogwartsUser> hogwartsUsers;

    @BeforeEach
    void setUp() {
        hogwartsUsers = List.of(
                new HogwartsUser()
                        .setId(1)
                        .setUsername("test001")
                        .setPassword("test001")
                        .setEnabled(true)
                        .setRoles("manager"),
                new HogwartsUser()
                        .setId(2)
                        .setUsername("test002")
                        .setPassword("test002")
                        .setEnabled(true)
                        .setRoles("user")
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllSuccess() {
        given(userRepository.findAll()).willReturn(hogwartsUsers);

        List<HogwartsUser> allUsers = userService.findAll();

        assertThat(allUsers.size()).isEqualTo(hogwartsUsers.size());
        assertThat(allUsers.get(0).getUsername()).isEqualTo(hogwartsUsers.get(0).getUsername());
        assertThat(allUsers.get(1).getUsername()).isEqualTo(hogwartsUsers.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findByIdSuccess() {
        given(userRepository.findById(anyInt())).willReturn(Optional.of(hogwartsUsers.get(0)));

        HogwartsUser user = userService.findById(1);

        assertThat(user.getUsername()).isEqualTo(hogwartsUsers.get(0).getUsername());
        assertThat(user.isEnabled()).isEqualTo(hogwartsUsers.get(0).isEnabled());
        assertThat(user.getRoles()).isEqualTo(hogwartsUsers.get(0).getRoles());
        verify(userRepository, times(1)).findById(anyInt());
    }

    @Test
    void findByIdErrorWhenIdNotExist() {
        given(userRepository.findById(1)).willThrow(new ObjectNotFoundException("user", 1));

        Throwable exception = catchThrowable(() -> userService.findById(1));

        assertThat(exception).isInstanceOf(ObjectNotFoundException.class);
        assertThat(exception).hasMessage("Could not find user with Id 1 :(");
    }

    @Test
    void saveSuccess() {
        HogwartsUser userWillBeSaved = hogwartsUsers.get(0);
        given(passwordEncoder.encode(userWillBeSaved.getPassword())).willReturn("Encoded Password");
        given(userRepository.save(userWillBeSaved)).willReturn(userWillBeSaved);

        HogwartsUser savedUser = userService.save(userWillBeSaved);

        assertThat(savedUser.getUsername()).isEqualTo(userWillBeSaved.getUsername());
        assertThat(savedUser.isEnabled()).isEqualTo(userWillBeSaved.isEnabled());
        assertThat(savedUser.getRoles()).isEqualTo(userWillBeSaved.getRoles());
        verify(userRepository, times(1)).save(userWillBeSaved);
    }

    @Test
    void updateSuccess() {
        HogwartsUser originUser = hogwartsUsers.get(0);
        HogwartsUser newUser = new HogwartsUser()
                .setId(1)
                .setUsername("new user")
                .setEnabled(false)
                .setRoles("blocked");

        given(userRepository.findById(1)).willReturn(Optional.of(originUser));
        given(userRepository.save(originUser)).willReturn(originUser);

        HogwartsUser userBeenSaved = userService.update(1, newUser);

        assertThat(userBeenSaved.getId()).isEqualTo(1);
        assertThat(userBeenSaved.getUsername()).isEqualTo("new user");
        assertThat(userBeenSaved.isEnabled()).isEqualTo(false);
        assertThat(userBeenSaved.getRoles()).isEqualTo("blocked");
    }

    @Test
    void updateErrorWhenIdNotExist() {
        given(userRepository.findById(1)).willThrow(new ObjectNotFoundException("user", 1));

        Throwable exception = catchThrowable(() -> userService.update(1, new HogwartsUser()));

        assertThat(exception).isInstanceOf(ObjectNotFoundException.class);
        assertThat(exception).hasMessage("Could not find user with Id 1 :(");
    }

    @Test
    void deleteSuccess() {
        given(userRepository.findById(1)).willReturn(Optional.of(hogwartsUsers.get(0)));
        doNothing().when(userRepository).deleteById(1);

        userService.deleteById(1);

        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteErrorWhenIdNotExist() {
        given(userRepository.findById(1)).willThrow(new ObjectNotFoundException("user", 1));

        Throwable exception = catchThrowable(() -> userService.deleteById(1));

        assertThat(exception).isInstanceOf(ObjectNotFoundException.class);
        assertThat(exception).hasMessage("Could not find user with Id 1 :(");
    }
}