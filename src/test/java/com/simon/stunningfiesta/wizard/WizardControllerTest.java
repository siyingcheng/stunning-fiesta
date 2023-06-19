package com.simon.stunningfiesta.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.stunningfiesta.artifact.Artifact;
import com.simon.stunningfiesta.system.StatusCode;
import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import com.simon.stunningfiesta.wizard.converters.WizardToWizardDtoConverter;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class WizardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WizardService wizardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WizardToWizardDtoConverter wizardToWizardDtoConverter;

    private List<Wizard> wizards;
    private Wizard albusDumledore;
    private Wizard harryPotter;
    private Wizard nevilleLongbottom;

    @Value("${api.endpoint.base-url}/wizards")
    private String baseUrl;

    @BeforeEach
    void setUp() {
        Artifact invisibilityCloak = new Artifact()
                .withName("Invisibility Cloak")
                .withDescription("An invisibility cloak is used to make the wearer invisible")
                .withImageUrl("ImageUrl");
        Artifact deliminator = new Artifact()
                .withName("Deliminator")
                .withDescription("An deliminator is a device invented by Albums Dumbledore than ...")
                .withImageUrl("ImageUrl");
        Artifact elderWand = new Artifact()
                .withName("Elder Wand")
                .withDescription("The Elder Wand, known throughout history as ...")
                .withImageUrl("ImageUrl");
        Artifact polymorph = new Artifact()
                .withName("Polymorph")
                .withDescription("The polymorph could turn the target to some animal ...")
                .withImageUrl("ImageUrl");

        albusDumledore = new Wizard()
                .withName("Albus Dumledore")
                .addArtifacts(elderWand, polymorph);
        harryPotter = new Wizard()
                .withName("Harry Potter")
                .addArtifacts(invisibilityCloak);
        nevilleLongbottom = new Wizard()
                .withName("Neville Longbottom")
                .addArtifacts(deliminator);

        wizards = List.of(albusDumledore, harryPotter, nevilleLongbottom);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllWizardsSuccess() throws Exception {
        given(wizardService.findAll()).willReturn(wizards);

        mockMvc.perform(get(this.baseUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data[0].name").value(albusDumledore.getName()))
                .andExpect(jsonPath("$.data[0].numbersOfArtifacts")
                        .value(albusDumledore.getNumberOfArtifacts()));
    }

    @Test
    void findWizardByIdSuccess() throws Exception {
        given(wizardService.findById(123)).willReturn(harryPotter);

        mockMvc.perform(get(this.baseUrl + "/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find Wizard Success"))
                .andExpect(jsonPath("$.data.name").value(harryPotter.getName()))
                .andExpect(jsonPath("$.data.numbersOfArtifacts")
                        .value(harryPotter.getNumberOfArtifacts()));
    }

    @Test
    void findWizardByIdFailWhenWizardIdNotExist() throws Exception {
        given(wizardService.findById(123)).willThrow(new ObjectNotFoundException("wizard", 123));

        mockMvc.perform(get(this.baseUrl + "/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 123 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addWizardSuccess() throws Exception {
        Wizard newWizard = new Wizard().withName("New Wizard");

        given(wizardService.save(any(Wizard.class))).willReturn(newWizard);

        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(post(this.baseUrl)
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Wizard Success"))
                .andExpect(jsonPath("$.data.name").value("New Wizard"));
    }

    @Test
    void addWizardFailWhenWizardDtoInvalid() throws Exception {
        Wizard newWizard = new Wizard().withName("");

        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(post(this.baseUrl)
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"));
    }

    @Test
    void updateWizardSuccess() throws Exception {
        Wizard newWizard = new Wizard().withName("New Wizard");

        given(wizardService.update(anyInt(), any(Wizard.class)))
                .willReturn(newWizard.withId(123));

        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(put(this.baseUrl + "/123")
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Wizard Success"))
                .andExpect(jsonPath("$.data.name").value("New Wizard"));
    }

    @Test
    void updateWizardFailWhenWizardIdNotExist() throws Exception {
        Wizard newWizard = new Wizard().withName("New Wizard");

        given(wizardService.update(anyInt(), any(Wizard.class)))
                .willThrow(new ObjectNotFoundException("wizard", 123));

        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(put(this.baseUrl + "/123")
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 123 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void updateWizardFailWhenWizardDtoInvalid() throws Exception {
        Wizard newWizard = new Wizard().withName("");

        WizardDto newWizardDto = wizardToWizardDtoConverter.convert(newWizard);

        mockMvc.perform(put(this.baseUrl + "/123")
                        .content(objectMapper.writeValueAsString(newWizardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.name").value("name is required"));
    }

    @Test
    void deleteWizardSuccess() throws Exception {
        Wizard newWizard = new Wizard().withName("New Wizard").withId(123);
        given(wizardService.findById(123)).willReturn(newWizard);

        mockMvc.perform(delete(this.baseUrl + "/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Wizard Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteWizardFailWhenWizardIdNotExist() throws Exception {
        doThrow(new ObjectNotFoundException("wizard", 123))
                .when(wizardService).deleteById(123);

        mockMvc.perform(delete(this.baseUrl + "/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 123 :("))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}