package com.simon.stunningfiesta.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.stunningfiesta.artifact.converts.ArtifactToArtifactDtoConverter;
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
@AutoConfigureMockMvc
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @Value("${api.endpoint.base-url}/artifacts")
    private String baseUrl;

    @BeforeEach
    void setUp() {
        Artifact deliminator = new Artifact()
                .withId(2)
                .withName("Deliminator")
                .withDescription("An deliminator is a device invented by Albums Dumbledore than ...")
                .withImageUrl("ImageUrl");
        Artifact invisibilityCloak = new Artifact()
                .withId(1)
                .withName("Invisibility Cloak")
                .withDescription("An invisibility cloak is used to make the wearer invisible")
                .withImageUrl("ImageUrl");
        Artifact elderWand = new Artifact()
                .withId(3)
                .withName("Elder Wand")
                .withDescription("The Elder Wand, known throughout history as ...")
                .withImageUrl("ImageUrl");
        artifacts = List.of(deliminator, invisibilityCloak, elderWand);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findArtifactByIdSuccess() throws Exception {
        given(artifactService.findById(2)).willReturn(artifacts.get(0));

        mockMvc.perform(get(this.baseUrl + "/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.name").value("Deliminator"));
    }

    @Test
    void findArtifactByIdNotFound() throws Exception {
        given(artifactService.findById(2))
                .willThrow(new ObjectNotFoundException("artifact", 2));

        mockMvc.perform(get(this.baseUrl + "/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 2 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void findAllArtifactsSuccess() throws Exception {
        given(artifactService.findAll()).willReturn(this.artifacts);

        mockMvc.perform(get(this.baseUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value(2));
    }

    @Test
    void saveArtifactSuccess() throws Exception {
        Artifact artifact = new Artifact()
                .withName("Test Artifact")
                .withDescription("Artifact's description...")
                .withImageUrl("ImageUrl");
        ArtifactDto artifactDto = artifactToArtifactDtoConverter.convert(artifact);

        String json = objectMapper.writeValueAsString(artifactDto);

        given(artifactService.save(any(Artifact.class))).willReturn(artifact);

        mockMvc.perform(post(this.baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.name").value("Test Artifact"))
                .andExpect(jsonPath("$.data.description").value("Artifact's description..."));
    }

    @Test
    void saveArtifactFailWhenParametersInvalid() throws Exception {
        String json = objectMapper.writeValueAsString(
                new ArtifactDto(null, "", "", "", null));

        mockMvc.perform(post(this.baseUrl)
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
    void updateArtifactSuccess() throws Exception {
        Artifact newArtifact = new Artifact()
                .withId(123)
                .withName("New Test Artifact")
                .withDescription("New newArtifact's description...")
                .withImageUrl("new imageUrl");
        ArtifactDto newArtifactDto = artifactToArtifactDtoConverter.convert(newArtifact);

        String json = objectMapper.writeValueAsString(newArtifactDto);

        given(artifactService.update(anyInt(), any(Artifact.class))).willReturn(newArtifact);

        mockMvc.perform(put(this.baseUrl + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.name").value("New Test Artifact"))
                .andExpect(jsonPath("$.data.description").value("New newArtifact's description..."));
    }

    @Test
    void updateArtifactFailWhenArtifactIdInvalid() throws Exception {
        Artifact newArtifact = new Artifact()
                .withId(123)
                .withName("New Test Artifact")
                .withDescription("New newArtifact's description...")
                .withImageUrl("new imageUrl");
        ArtifactDto newArtifactDto = artifactToArtifactDtoConverter.convert(newArtifact);

        String json = objectMapper.writeValueAsString(newArtifactDto);

        given(artifactService.update(anyInt(), any(Artifact.class)))
                .willThrow(new ObjectNotFoundException("artifact", 123));

        mockMvc.perform(put(this.baseUrl + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 123 :("));
    }

    @Test
    void updateArtifactFailWhenArtifactDtoInvalid() throws Exception {
        Artifact newArtifact = new Artifact()
                .withId(123)
                .withName("")
                .withDescription("New newArtifact's description...")
                .withImageUrl("new imageUrl");
        ArtifactDto newArtifactDto = artifactToArtifactDtoConverter.convert(newArtifact);

        String json = objectMapper.writeValueAsString(newArtifactDto);

        mockMvc.perform(put(this.baseUrl + "/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"));
    }

    @Test
    void deleteArtifactSuccess() throws Exception {
        doNothing().when(artifactService).deleteById(123);

        mockMvc.perform(delete(this.baseUrl + "/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteArtifactFailWhenArtifactIdNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("artifact", 123))
                .when(artifactService).deleteById(123);

        mockMvc.perform(delete(this.baseUrl + "/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 123 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}