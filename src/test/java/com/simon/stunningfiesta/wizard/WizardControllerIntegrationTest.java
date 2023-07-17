package com.simon.stunningfiesta.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.stunningfiesta.system.StatusCode;
import com.simon.stunningfiesta.wizard.converters.WizardToWizardDtoConverter;
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
@DisplayName("Integration test for wizard API endpoint.")
@Tag("integration")
public class WizardControllerIntegrationTest {
    private static final int DEFAULT_WIZARD_NUM = 3;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    private String baseUrl;

    @Value("${api.endpoint.base-url}/wizards")
    private String wizardsUrl;

    private String token;
    @Autowired
    private WizardToWizardDtoConverter wizardToWizardDtoConverter;

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
    @DisplayName("Verify find all wizard success")
    void findAllWizardsSuccess() throws Exception {
        mockMvc.perform(get(this.wizardsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_WIZARD_NUM)));
    }

    @Test
    @DisplayName("Verify find wizard by ID success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findWizardByIdSuccess() throws Exception {
        mockMvc.perform(get(this.wizardsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find Wizard Success"))
                .andExpect(jsonPath("$.data.name").value("Albus Dumledore"));
    }

    @Test
    @DisplayName("Verify find wizard by ID error when wizard ID not exist")
    void findWizardByIdErrorWhenWizardIdNotExist() throws Exception {
        mockMvc.perform(get(this.wizardsUrl + "/999")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 999 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Verify add wizard success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addWizardSuccess() throws Exception {
        Wizard newWizard = new Wizard().withName("New Wizard");

        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(post(this.wizardsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Wizard Success"))
                .andExpect(jsonPath("$.data.name").value("New Wizard"));

        mockMvc.perform(get(this.wizardsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_WIZARD_NUM + 1)));
    }

    @Test
    @DisplayName("Verify add wizard error when wizard entry parameters invalid")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void addWizardErrorWhenWizardDtoInvalid() throws Exception {
        Wizard newWizard = new Wizard().withName("");

        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(post(this.wizardsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"));

        mockMvc.perform(get(this.wizardsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_WIZARD_NUM)));
    }

    @Test
    @DisplayName("Verify update wizard success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateWizardSuccess() throws Exception {
        Wizard newWizard = new Wizard().withName("New Wizard");
        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(put(this.wizardsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Wizard Success"))
                .andExpect(jsonPath("$.data.name").value("New Wizard"));

        mockMvc.perform(get(this.wizardsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find Wizard Success"))
                .andExpect(jsonPath("$.data.name").value("New Wizard"));
    }

    @Test
    @DisplayName("Verify update wizard error when wizard ID not exist")
    void updateWizardErrorWhenWizardIdNotExist() throws Exception {
        Wizard newWizard = new Wizard().withName("New Wizard");
        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(put(this.wizardsUrl + "/123")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 123 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Verify update wizard error when wizard entry parameter invalid")
    void updateWizardErrorWhenWizardParameterInvalid() throws Exception {
        Wizard newWizard = new Wizard().withName("");
        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(put(this.wizardsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"));
    }

    @Test
    @DisplayName("Verify delete wizard success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteWizardSuccess() throws Exception {
        mockMvc.perform(delete(this.wizardsUrl + "/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Wizard Success"))
                .andExpect(jsonPath("$.data").isEmpty());

        mockMvc.perform(get(this.wizardsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_WIZARD_NUM - 1)));
    }

    @Test
    @DisplayName("Verify delete wizard error when wizard Id not exist")
    void deleteWizardErrorWhenWizardIdNotExist() throws Exception {
        mockMvc.perform(delete(this.wizardsUrl + "/123")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 123 :("))
                .andExpect(jsonPath("$.data").isEmpty());

        mockMvc.perform(get(this.wizardsUrl)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(DEFAULT_WIZARD_NUM)));
    }

    @Test
    @DisplayName("Verify assign artifact success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void assignArtifactSuccess() throws Exception {
        mockMvc.perform(put(this.wizardsUrl + "/2/artifacts/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Assign Artifact Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Verify assign artifact error when wizard ID not exist")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void assignArtifactErrorWhenWizardIdNotExist() throws Exception {
        mockMvc.perform(put(this.wizardsUrl + "/999/artifacts/1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 999 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Verify assign artifact success")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void assignArtifactErrorWhenArtifactIdNotExist() throws Exception {
        mockMvc.perform(put(this.wizardsUrl + "/2/artifacts/999")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 999 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
