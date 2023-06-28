package com.simon.stunningfiesta.wizard;

import com.simon.stunningfiesta.artifact.Artifact;
import com.simon.stunningfiesta.artifact.ArtifactRepository;
import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WizardService {
    private final WizardRepository wizardRepository;

    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public void deleteById(Integer id) {
        findById(id);
        wizardRepository.deleteById(id);
    }

    public Wizard save(Wizard newWizard) {
        return wizardRepository.save(newWizard);
    }

    public List<Wizard> findAll() {
        return wizardRepository.findAll();
    }

    public Wizard findById(Integer id) {
        return wizardRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", id));
    }

    public Wizard update(Integer id, Wizard newWizard) {
        return wizardRepository.findById(id)
                .map(wizard -> {
                    wizard.setName(newWizard.getName());
                    return wizardRepository.save(wizard);
                })
                .orElseThrow(() -> new ObjectNotFoundException("wizard", id));
    }

    public void assignArtifact(Integer wizardId, Integer artifactId) {
        Artifact artifactToBeAssigned = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
        Wizard wizard = findById(wizardId);

        Optional.of(artifactToBeAssigned.getOwner())
                .ifPresent(ownerWilBeRemoved -> ownerWilBeRemoved.removeArtifact(artifactToBeAssigned));
        wizard.addArtifacts(artifactToBeAssigned);
    }
}
