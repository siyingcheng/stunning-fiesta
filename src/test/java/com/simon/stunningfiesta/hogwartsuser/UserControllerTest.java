package com.simon.stunningfiesta.hogwartsuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.stunningfiesta.hogwartsuser.converters.UserDtoToUserConverter;
import com.simon.stunningfiesta.hogwartsuser.converters.UserToUserDtoConverter;
import com.simon.stunningfiesta.system.StatusCode;
import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    UserToUserDtoConverter userToUserDtoConverter;

    @Autowired
    UserDtoToUserConverter userDtoToUserConverter;

    @Autowired
    ObjectMapper objectMapper;

    List<HogwartsUser> users;

    @Value("${api.endpoint.base-url}/users")
    String baseUrl;

    @BeforeEach
    void setUp() {
        users = List.of(
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
    void findAllUsersSuccess() throws Exception {
        given(userService.findAll()).willReturn(users);

        mockMvc.perform(get(this.baseUrl).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(users.size())))
                .andExpect(jsonPath("$.data[0].username").value(users.get(0).getUsername()))
                .andExpect(jsonPath("$.data[0].password").doesNotHaveJsonPath());
    }

    @Test
    void findUserByIdSuccess() throws Exception {
        given(userService.findById(1)).willReturn(users.get(0));

        UserDto userDto = userToUserDtoConverter.convert(users.get(0));

        mockMvc.perform(get(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.username").value(userDto.username()))
                .andExpect(jsonPath("$.data.enabled").value(userDto.enabled()))
                .andExpect(jsonPath("$.data.roles").value(userDto.roles()))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void findUserByIdErrorWhenUserIdNotExist() throws Exception {
        given(userService.findById(1)).willThrow(new ObjectNotFoundException("user", 1));

        mockMvc.perform(get(this.baseUrl + "/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1 :("))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void addUserSuccess() throws Exception {
        HogwartsUser hogwartsUser = users.get(0);
        given(userService.save(any(HogwartsUser.class))).willReturn(hogwartsUser);

        mockMvc.perform(post(this.baseUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hogwartsUser)))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add User Success"))
                .andExpect(jsonPath("$.data.username").value(hogwartsUser.getUsername()))
                .andExpect(jsonPath("$.data.enabled").value(hogwartsUser.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(hogwartsUser.getRoles()))
                .andExpect(jsonPath("$.data.password").doesNotHaveJsonPath());
    }

    @Test
    void addUserErrorWhenParametersInvalid() throws Exception {

        mockMvc.perform(post(this.baseUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HogwartsUser())))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.username").value("username is required."))
                .andExpect(jsonPath("$.data.roles").value("roles are required."))
                .andExpect(jsonPath("$.data.password").value("password is required."));
    }

    @Test
    void updateUserSuccess() throws Exception {
        HogwartsUser hogwartsUser = users.get(0);
        HogwartsUser userUpdated = new HogwartsUser()
                .setId(hogwartsUser.getId())
                .setUsername("Updated " + hogwartsUser.getUsername())
                .setPassword(hogwartsUser.getPassword())
                .setEnabled(false)
                .setRoles(hogwartsUser.getRoles());

        given(userService.update(anyInt(), any(HogwartsUser.class))).willReturn(userUpdated);

        mockMvc.perform(put(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdated)))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update User Success"))
                .andExpect(jsonPath("$.data.username").value(userUpdated.getUsername()))
                .andExpect(jsonPath("$.data.enabled").value(userUpdated.isEnabled()))
                .andExpect(jsonPath("$.data.roles").value(userUpdated.getRoles()))
                .andExpect(jsonPath("$.data.password").doesNotHaveJsonPath());
    }

    @Test
    void updateUserErrorWhenParametersInvalid() throws Exception {

        mockMvc.perform(put(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HogwartsUser())))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.username").value("username is required."))
                .andExpect(jsonPath("$.data.roles").value("roles are required."))
                .andExpect(jsonPath("$.data.password").value("password is required."));
    }

    @Test
    void updateUserErrorWhenUserIdNotExist() throws Exception {

        given(userService.update(anyInt(), any(HogwartsUser.class))).willThrow(new ObjectNotFoundException("user", 1));

        mockMvc.perform(put(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(users.get(0))))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1 :("))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void deleteUserSuccess() throws Exception {

        doNothing().when(userService).deleteById(anyInt());

        mockMvc.perform(delete(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete User Success"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    void deleteUserErrorWhenUserIdNotExist() throws Exception {

        doThrow(new ObjectNotFoundException("user", 1))
                .when(userService).deleteById(anyInt());

        mockMvc.perform(delete(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 1 :("))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }
}