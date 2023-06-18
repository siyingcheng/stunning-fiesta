package com.simon.stunningfiesta.artifact;

import com.simon.stunningfiesta.wizard.Wizard;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    ArtifactService artifactService;

    private List<Artifact> artifacts;

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
        artifacts = List.of(invisibilityCloak, deliminator);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        Integer artifactId = 1;
        Artifact invisibilityCloak = new Artifact()
                .withId(artifactId)
                .withName("Invisibility Cloak")
                .withDescription("An invisibility cloak is used to make the wearer invisible")
                .withImageUrl("ImageUrl");

        Wizard harryPotter = new Wizard()
                .withId(2)
                .withName("Harry Potter")
                .withArtifacts(List.of(invisibilityCloak));

        invisibilityCloak.setOwner(harryPotter);

        given(artifactRepository.findById(artifactId)).willReturn(Optional.of(invisibilityCloak));

        Artifact returnArtifact = artifactService.findById(artifactId);

        assertThat(returnArtifact.getId().equals(invisibilityCloak.getId())).isTrue();
        assertThat(returnArtifact.getName().equals(invisibilityCloak.getName())).isTrue();
        assertThat(returnArtifact.getDescription().equals(invisibilityCloak.getDescription())).isTrue();
        assertThat(returnArtifact.getImageUrl().equals(invisibilityCloak.getImageUrl())).isTrue();
        assertThat(returnArtifact.getOwner().equals(invisibilityCloak.getOwner())).isTrue();

        verify(artifactRepository, times(1)).findById(artifactId);
    }


    @Test
    void testFindByIdNotFound() {
        Integer artifactId = 1;
        given(artifactRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> artifactService.findById(artifactId));

        assertThat(thrown)
                .isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Could not find artifact with Id 1 :(");
    }

    @Test
    void testFindAllSuccess() {
        given(artifactRepository.findAll()).willReturn(this.artifacts);

        List<Artifact> actualArtifacts = artifactService.findAll();

        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        Artifact artifact = new Artifact()
                .withName("Test Artifact")
                .withDescription("Artifact's description...")
                .withImageUrl("Image URL...");

        given(artifactRepository.save(artifact)).willReturn(artifact);

        Artifact savedArtifact = artifactService.save(artifact);

        assertThat(savedArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(artifact.getDescription());
        verify(artifactRepository, times(1)).save(artifact);
    }

    @Test
    void testUpdateSuccess() {
        Artifact artifact = new Artifact()
                .withId(123)
                .withName("Test Artifact")
                .withDescription("Artifact's description...")
                .withImageUrl("Image URL...");
        Artifact newArtifact = new Artifact()
                .withId(123)
                .withName("New Test Artifact")
                .withDescription("New artifact's description...")
                .withImageUrl("New image URL...");

        given(artifactRepository.findById(123)).willReturn(Optional.of(artifact));
        given(artifactRepository.save(artifact)).willReturn(newArtifact);

        Artifact updatedArtifact = artifactService.update(123, newArtifact);

        assertThat(updatedArtifact.getId()).isEqualTo(artifact.getId());
        assertThat(updatedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(updatedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(updatedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
        verify(artifactRepository, times(1)).findById(123);
        verify(artifactRepository, times(1)).save(artifact);
    }

    @Test
    void testUpdateFailWhenArtifactIdNotExist() {
        Artifact newArtifact = new Artifact()
                .withId(123)
                .withName("New Test Artifact")
                .withDescription("New artifact's description...")
                .withImageUrl("New image URL...");

        given(artifactRepository.findById(123))
                .willThrow(new ArtifactNotFoundException(123));

        Throwable exception = catchThrowable(() -> artifactService.update(123, newArtifact));

        assertThat(exception.getMessage()).isEqualTo("Could not find artifact with Id 123 :(");
        verify(artifactRepository, times(1)).findById(123);
    }

    @Test
    void testDeleteSuccess() {
        Artifact artifact = new Artifact()
                .withId(123)
                .withName("Test Artifact")
                .withDescription("Artifact's description...")
                .withImageUrl("Image URL...");
        given(artifactRepository.findById(123)).willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById(123);

        artifactService.deleteById(123);

        verify(artifactRepository, times(1)).deleteById(123);
    }

    @Test
    void testDeleteFailWhenIdNotExist() {
        given(artifactRepository.findById(123)).willReturn(Optional.empty());

        assertThrows(ArtifactNotFoundException.class,
                () -> artifactService.deleteById(123));

        verify(artifactRepository, times(1)).findById(123);
    }
}