package com.simon.stunningfiesta.wizard;

import com.simon.stunningfiesta.artifact.Artifact;
import com.simon.stunningfiesta.artifact.ArtifactRepository;
import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    @Mock
    private WizardRepository wizardRepository;

    @Mock
    private ArtifactRepository artifactRepository;

    @InjectMocks
    private WizardService wizardService;
    private List<Wizard> wizards;
    private Wizard albusDumledore;
    private Wizard harryPotter;
    private Wizard nevilleLongbottom;

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
    public void testFindAllSuccess() {
        given(wizardRepository.findAll()).willReturn(wizards);

        List<Wizard> allWizards = wizardService.findAll();

        assertThat(allWizards.size()).isEqualTo(3);
        assertThat(allWizards.get(0).getName()).isEqualTo("Albus Dumledore");
        assertThat(allWizards.get(0).getArtifacts().get(0).getName()).isEqualTo("Elder Wand");
        assertThat(allWizards.get(0).getArtifacts().get(0).getDescription()).isEqualTo("The Elder Wand, known throughout history as ...");
        verify(wizardRepository, times(1)).findAll();
    }

    @Test
    public void testFindByIdSuccess() {
        given(wizardRepository.findById(2)).willReturn(Optional.of(harryPotter));

        Wizard wizard = wizardService.findById(2);

        assertThat(wizard.getName()).isEqualTo("Harry Potter");
        assertThat(wizard.getArtifacts().get(0).getName()).isEqualTo("Invisibility Cloak");
        assertThat(wizard.getArtifacts().get(0).getDescription())
                .isEqualTo("An invisibility cloak is used to make the wearer invisible");
        verify(wizardRepository, times(1)).findById(2);
    }

    @Test
    public void testFindByIdFailWhenIdNotExist() {
        given(wizardRepository.findById(2)).willReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> wizardService.findById(2));

        assertThat(exception.getMessage()).isEqualTo("Could not find wizard with Id 2 :(");
        verify(wizardRepository, times(1)).findById(2);
    }

    @Test
    public void testAddSuccess() {
        Wizard newWizard = new Wizard().withName("New wizard")
                .withArtifacts(List.of(new Artifact()
                        .withName("Polymorph")
                        .withDescription("The polymorph could turn the target to some animal ...")
                        .withImageUrl("ImageUrl")));

        given(wizardRepository.save(newWizard)).willReturn(newWizard);

        Wizard addedWizard = wizardService.save(newWizard);

        assertThat(addedWizard.getName()).isEqualTo("New wizard");
        assertThat(addedWizard.getArtifacts().get(0).getName())
                .isEqualTo("Polymorph");
        assertThat(addedWizard.getArtifacts().get(0).getDescription())
                .isEqualTo("The polymorph could turn the target to some animal ...");
        assertThat(addedWizard.getArtifacts().get(0).getImageUrl())
                .isEqualTo("ImageUrl");
        verify(wizardRepository, times(1)).save(newWizard);
    }

    @Test
    public void testUpdateSuccess() {
        Wizard wizard = new Wizard()
                .withName("Wizard")
                .withId(123);
        Wizard newWizard = new Wizard()
                .withName("New wizard")
                .withId(123);

        given(wizardRepository.findById(123)).willReturn(Optional.of(wizard));
        given(wizardRepository.save(wizard)).willReturn(wizard);

        Wizard updatedWizard = wizardService.update(123, newWizard);

        assertThat(updatedWizard.getName()).isEqualTo("New wizard");
        assertThat(updatedWizard.getArtifacts()).isEmpty();
        verify(wizardRepository, times(1)).findById(123);
        verify(wizardRepository, times(1)).save(wizard);
    }

    @Test
    public void testUpdateFailWhenIdNotExist() {
        Wizard newWizard = new Wizard()
                .withName("New wizard")
                .withId(123);
        given(wizardRepository.findById(123)).willReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> wizardService.update(123, newWizard));

        assertThat(exception.getMessage()).isEqualTo("Could not find wizard with Id 123 :(");
        verify(wizardRepository, times(1)).findById(123);
        verify(wizardRepository, times(0)).save(any(Wizard.class));
    }

    @Test
    public void testDeleteSuccess() {
        given(wizardRepository.findById(123)).willReturn(Optional.of(harryPotter));
        doNothing().when(wizardRepository).deleteById(123);

        wizardService.deleteById(123);

        verify(wizardRepository, times(1)).findById(123);
        verify(wizardRepository, times(1)).deleteById(123);
    }

    @Test
    public void testDeleteFailWhenIdNotExist() {
        given(wizardRepository.findById(123)).willReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> wizardService.deleteById(123));

        assertThat(exception.getMessage()).isEqualTo("Could not find wizard with Id 123 :(");
        verify(wizardRepository, times(0)).deleteById(123);
    }

    @Test
    public void testAssignArtifactSuccess() {
        Artifact invisibilityCloak = new Artifact()
                .withId(1)
                .withName("Invisibility Cloak")
                .withDescription("An invisibility cloak is used to make the wearer invisible")
                .withImageUrl("ImageUrl");

        Wizard albusDumledore = new Wizard()
                .withId(1)
                .withName("Albus Dumledore")
                .addArtifacts(invisibilityCloak);
        Wizard harryPotter = new Wizard()
                .withId(2)
                .withName("Harry Potter");

        given(artifactRepository.findById(1)).willReturn(Optional.of(invisibilityCloak));
        given(wizardRepository.findById(2)).willReturn(Optional.of(harryPotter));
        // when
        wizardService.assignArtifact(2, 1);
        // then
        assertThat(invisibilityCloak.getOwner().getId()).isEqualTo(2);
        assertThat(harryPotter.getArtifacts()).contains(invisibilityCloak);
        assertThat(albusDumledore.getArtifacts()).doesNotContain(invisibilityCloak);
    }

    @Test
    public void testAssignArtifactFailWhenWizardIdNotExist() {
        Artifact invisibilityCloak = new Artifact()
                .withId(1)
                .withName("Invisibility Cloak")
                .withDescription("An invisibility cloak is used to make the wearer invisible")
                .withImageUrl("ImageUrl");

        Wizard albusDumledore = new Wizard()
                .withId(1)
                .withName("Albus Dumledore")
                .addArtifacts(invisibilityCloak);

        given(artifactRepository.findById(1)).willReturn(Optional.of(invisibilityCloak));
        given(wizardRepository.findById(2)).willReturn(Optional.empty());
        // when
        Throwable exception = catchThrowable(() -> wizardService.assignArtifact(2, 1));
        // then
        assertThat(invisibilityCloak.getOwner().getId()).isEqualTo(albusDumledore.getId());
        assertThat(exception).hasMessage("Could not find wizard with Id 2 :(");
    }

    @Test
    public void testAssignArtifactFailWhenArtifactIdNotExist() {
        given(artifactRepository.findById(1)).willReturn(Optional.empty());
        // when
        Throwable exception = catchThrowable(() -> wizardService.assignArtifact(2, 1));
        // then
        assertThat(exception).hasMessage("Could not find artifact with Id 1 :(");
    }
}