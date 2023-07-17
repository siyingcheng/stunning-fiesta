package com.simon.stunningfiesta.hogwartsuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.stunningfiesta.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Verify authentication and authorization")
@Tag("integration")
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}/users/login")
    String loginUrl;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Test
    @DisplayName("Verify login success with valid username and password")
    void testLoginSuccess() throws Exception {
        mockMvc.perform(post(loginUrl).with(httpBasic("simon", "123456"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User Info and JSON Web Token"))
                .andExpect(jsonPath("$.data.userInfo.username").value("simon"))
                .andExpect(jsonPath("$.data.userInfo.enabled").value(true))
                .andExpect(jsonPath("$.data.userInfo.roles").value("admin user"))
                .andExpect(jsonPath("$.data.token", Matchers.matchesPattern("^([0-9a-zA-Z\\-_])+\\.([0-9a-zA-Z\\-_])+\\.([0-9a-zA-Z\\-_])+$")));
    }

    @Test
    @DisplayName("Verify login error with invalid username")
    void testLoginErrorWhenUsernameInvalid() throws Exception {
        mockMvc.perform(post(loginUrl).with(httpBasic("simon999", "123456"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect."))
                .andExpect(jsonPath("$.data").value("Bad credentials"));
    }

    @Test
    @DisplayName("Verify login error with invalid password")
    void testLoginErrorWhenPasswordInvalid() throws Exception {
        mockMvc.perform(post(loginUrl).with(httpBasic("simon", "test"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username or password is incorrect."))
                .andExpect(jsonPath("$.data").value("Bad credentials"));
    }

    @Test
    @DisplayName("Verify login error without basic authentication")
    void testLoginErrorWithoutBasicAuthentication() throws Exception {
        mockMvc.perform(post(loginUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("username and password are mandatory."))
                .andExpect(jsonPath("$.data").value("Full authentication is required to access this resource"));
    }

    @Test
    @DisplayName("Verify login error with a disabled user")
    void testLoginErrorWithADisabledUser() throws Exception {
        mockMvc.perform(post(loginUrl)
                        .with(httpBasic("user2", "123456"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("User account is abnormal."))
                .andExpect(jsonPath("$.data").value("User is disabled"));
    }

    @Test
    @DisplayName("Verify request error with invalid bearer token")
    void testRequestErrorWithInvalidBearerToken() throws Exception {
        mockMvc.perform(get(baseUrl + "/wizards")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer error_token.error_token.error_token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.UNAUTHORIZED))
                .andExpect(jsonPath("$.message").value("The access token provided is expired, revoked, malformed, or invalid for other reasons."));
    }

    @Test
    @DisplayName("Verify request users endpoint error when login with a none admin user")
    void testRequestUsersEndpointErrorWhenLoginWithANoneAdminUser() throws Exception {
        String responseBody = mockMvc.perform(post(loginUrl)
                        .with(httpBasic("user1", "123456"))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = "Bearer " + new JSONObject(responseBody).getJSONObject("data").getString("token");

        // GET users
        mockMvc.perform(get(baseUrl + "/users")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("No permission."))
                .andExpect(jsonPath("$.data").value("Access Denied"));

        // GET users By ID
        mockMvc.perform(get(baseUrl + "/users/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("No permission."))
                .andExpect(jsonPath("$.data").value("Access Denied"));

        // POST user
        HogwartsUser hogwartsUser = new HogwartsUser()
                .setUsername("test")
                .setPassword("test")
                .setEnabled(true)
                .setRoles("user");
        mockMvc.perform(post(baseUrl + "/users")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(hogwartsUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("No permission."))
                .andExpect(jsonPath("$.data").value("Access Denied"));

        // PUT user
        mockMvc.perform(put(baseUrl + "/users/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(hogwartsUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("No permission."))
                .andExpect(jsonPath("$.data").value("Access Denied"));

        // DELETE user
        mockMvc.perform(delete(baseUrl + "/users/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("No permission."))
                .andExpect(jsonPath("$.data").value("Access Denied"));
    }
}
