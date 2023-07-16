package com.simon.stunningfiesta.hogwartsuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.stunningfiesta.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration test for user API endpoint")
@Tag("integration")
public class UserControllerIntegrationTest {
    private static final int DEFAULT_USER_NUM = 3;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}/users")
    private String usersUrl;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post(usersUrl + "/login")
                .with(httpBasic("simon", "123456"))
                .accept(MediaType.APPLICATION_JSON));
        String content = resultActions.andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        this.token = "Bearer " + new JSONObject(content).getJSONObject("data").getString("token");
    }

    @Test
    @DisplayName("Verify find all users success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findAllUsersSuccess() throws Exception {
        mockMvc.perform(get(this.usersUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(DEFAULT_USER_NUM)));
    }

    @Test
    @DisplayName("Verify find user by ID success")
    void findUserByIdSuccess() throws Exception {
        mockMvc.perform(get(this.usersUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.username").value("simon"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("admin user"));
    }

    @Test
    @DisplayName("Verify find user by ID error when user ID not exist")
    void findUserByIdErrorWhenUserIdNotExist() throws Exception {
        mockMvc.perform(get(this.usersUrl + "/999")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 999 :("))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("Verify add user success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addUserSuccess() throws Exception {
        HogwartsUser hogwartsUser = new HogwartsUser()
                .setUsername("new-user")
                .setPassword("123456")
                .setEnabled(true)
                .setRoles("user");

        mockMvc.perform(post(this.usersUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
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

        mockMvc.perform(get(this.usersUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(DEFAULT_USER_NUM + 1)));
    }

    @Test
    @DisplayName("Verify add user error when user parameters invalid")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addUserErrorWhenParametersInvalid() throws Exception {
        mockMvc.perform(post(this.usersUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HogwartsUser())))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.username").value("username is required."))
                .andExpect(jsonPath("$.data.roles").value("roles are required."))
                .andExpect(jsonPath("$.data.password").value("password is required."));

        mockMvc.perform(get(this.usersUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(DEFAULT_USER_NUM)));
    }

    @Test
    @DisplayName("Verify update user success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUserSuccess() throws Exception {
        HogwartsUser userUpdated = new HogwartsUser()
                .setUsername("updated-user")
                .setPassword("updated-user")
                .setEnabled(false)
                .setRoles("admin");

        mockMvc.perform(put(this.usersUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
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

        mockMvc.perform(get(this.usersUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.username").value("updated-user"))
                .andExpect(jsonPath("$.data.enabled").value(false))
                .andExpect(jsonPath("$.data.roles").value("admin"));
    }

    @Test
    @DisplayName("Verify update user error when user parameter invalid")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUserErrorWhenParametersInvalid() throws Exception {
        mockMvc.perform(put(this.usersUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new HogwartsUser())))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.username").value("username is required."))
                .andExpect(jsonPath("$.data.roles").value("roles are required."))
                .andExpect(jsonPath("$.data.password").value("password is required."));

        mockMvc.perform(get(this.usersUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.username").value("simon"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("admin user"));
    }

    @Test
    @DisplayName("Verify update user error when user ID not exist")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateUserErrorWhenUserIdNotExist() throws Exception {
        HogwartsUser userUpdated = new HogwartsUser()
                .setUsername("updated-user")
                .setPassword("updated-user")
                .setEnabled(false)
                .setRoles("admin");

        mockMvc.perform(put(this.usersUrl + "/999")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdated)))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 999 :("))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        mockMvc.perform(get(this.usersUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.username").value("simon"))
                .andExpect(jsonPath("$.data.enabled").value(true))
                .andExpect(jsonPath("$.data.roles").value("admin user"));
    }

    @Test
    @DisplayName("Verify delete user success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteUserSuccess() throws Exception {
        mockMvc.perform(delete(this.usersUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete User Success"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        mockMvc.perform(get(this.usersUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(DEFAULT_USER_NUM - 1)));
    }

    @Test
    @DisplayName("Verify delete user error when user ID not exist")
    void deleteUserErrorWhenUserIdNotExist() throws Exception {
        mockMvc.perform(delete(this.usersUrl + "/999")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 999 :("))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        mockMvc.perform(get(this.usersUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value("true"))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data").value(Matchers.hasSize(DEFAULT_USER_NUM)));
    }
}
