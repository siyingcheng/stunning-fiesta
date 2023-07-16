package com.simon.stunningfiesta.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.stunningfiesta.artifact.converts.ArtifactToArtifactDtoConverter;
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
@DisplayName("Integration test for artifact API endpoints.")
@Tag("integration")
public class ArtifactControllerIntegrationTest {
    private static final int DEFAULT_ARTIFACT_NUM = 5;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    @Value("${api.endpoint.base-url}/artifacts")
    private String artifactsUrl;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post(baseUrl + "/users/login")
                .with(httpBasic("simon", "123456"))
                .accept(MediaType.APPLICATION_JSON));
        String content = resultActions.andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        this.token = "Bearer " + new JSONObject(content).getJSONObject("data").getString("token");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    // Reset H2 database before calling this test case.
    @DisplayName("Verify find all artifacts success")
    void findAllArtifactsSuccess() throws Exception {
        mockMvc.perform(get(this.artifactsUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_ARTIFACT_NUM)));
    }

    @Test
    @DisplayName("Verify find artifact by ID success")
    void findArtifactByIdSuccess() throws Exception {
        mockMvc.perform(get(this.artifactsUrl + "/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.name").value("Polymorph"));
    }

    @Test
    @DisplayName("Verify find artifact by ID error when artifactId not exist")
    void findArtifactByIdErrorWhenArtifactIdNotExist() throws Exception {
        mockMvc.perform(get(this.artifactsUrl + "/9999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 9999 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Verify save artifact success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
        // Reset H2 database before calling this test case.
    void saveArtifactSuccess() throws Exception {
        Artifact artifact = new Artifact()
                .withName("Test Artifact")
                .withDescription("Artifact's description...")
                .withImageUrl("ImageUrl");
        ArtifactDto artifactDto = artifactToArtifactDtoConverter.convert(artifact);

        String json = objectMapper.writeValueAsString(artifactDto);

        mockMvc.perform(post(this.artifactsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.name").value("Test Artifact"))
                .andExpect(jsonPath("$.data.description").value("Artifact's description..."));

        mockMvc.perform(get(this.artifactsUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_ARTIFACT_NUM + 1)));
    }

    @Test
    @DisplayName("Verify save artifact error when parameters invalid")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
        // Reset H2 database before calling this test case.
    void saveArtifactErrorWhenParameterInvalid() throws Exception {
        String json = objectMapper.writeValueAsString(
                new ArtifactDto(null, "", "", "", null));

        mockMvc.perform(post(this.artifactsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"))
                .andExpect(jsonPath("$.data.description").value("description is required"))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required"));

        mockMvc.perform(get(this.artifactsUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_ARTIFACT_NUM)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    // Reset H2 database before calling this test case.
    @DisplayName("Verify update artifact success")
    void updateArtifactSuccess() throws Exception {
        Artifact newArtifact = new Artifact()
                .withName("New Test Artifact")
                .withDescription("New newArtifact's description...")
                .withImageUrl("new imageUrl");
        ArtifactDto newArtifactDto = artifactToArtifactDtoConverter.convert(newArtifact);

        String json = objectMapper.writeValueAsString(newArtifactDto);

        mockMvc.perform(put(this.artifactsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.name").value("New Test Artifact"))
                .andExpect(jsonPath("$.data.description").value("New newArtifact's description..."));

        mockMvc.perform(get(this.artifactsUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("New Test Artifact"))
                .andExpect(jsonPath("$.data.description").value("New newArtifact's description..."));
        ;
    }

    @Test
    @DisplayName("Verify update artifact error when artifact ID not exist")
    void updateArtifactErrorWhenArtifactIdNotExist() throws Exception {
        Artifact newArtifact = new Artifact()
                .withName("New Test Artifact")
                .withDescription("New newArtifact's description...")
                .withImageUrl("new imageUrl");
        ArtifactDto newArtifactDto = artifactToArtifactDtoConverter.convert(newArtifact);

        String json = objectMapper.writeValueAsString(newArtifactDto);

        mockMvc.perform(put(this.artifactsUrl + "/999")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 999 :("));
    }

    @Test
    @DisplayName("Verify update artifact error when artifact entry parameters invalid")
    void updateArtifactErrorWhenArtifactEntryParametersInvalid() throws Exception {
        Artifact newArtifact = new Artifact()
                .withName("")
                .withDescription("")
                .withImageUrl("");
        ArtifactDto newArtifactDto = artifactToArtifactDtoConverter.convert(newArtifact);

        String json = objectMapper.writeValueAsString(newArtifactDto);

        mockMvc.perform(put(this.artifactsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"))
                .andExpect(jsonPath("$.data.description").value("description is required"))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl is required"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Verify delete artifact success")
    void deleteArtifactSuccess() throws Exception {
        mockMvc.perform(delete(this.artifactsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());

        mockMvc.perform(get(this.artifactsUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_ARTIFACT_NUM - 1)));
    }

    @Test
    @DisplayName("Verify delete artifact error when artifact ID not exist")
    void deleteArtifactErrorWhenArtifactIdNotExist() throws Exception {
        mockMvc.perform(delete(this.artifactsUrl + "/999")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 999 :("));

        mockMvc.perform(get(this.artifactsUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_ARTIFACT_NUM)));
    }
}
