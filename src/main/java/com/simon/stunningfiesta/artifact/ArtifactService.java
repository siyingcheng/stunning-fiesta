package com.simon.stunningfiesta.artifact;

import com.simon.stunningfiesta.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {
    private final ArtifactRepository artifactRepository;

    public ArtifactService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public void deleteById(Integer id) {
        findById(id);
        artifactRepository.deleteById(id);
    }

    public Artifact findById(Integer artifactId) {
        return artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    }

    public List<Artifact> findAll() {
        return artifactRepository.findAll();
    }

    public Artifact save(Artifact artifact) {
        return artifactRepository.save(artifact);
    }

    public Artifact update(Integer artifactId, Artifact newArtifact) {
        return artifactRepository.findById(artifactId)
                .map(artifact -> {
                    artifact.withName(newArtifact.getName())
                            .withDescription(newArtifact.getDescription())
                            .withImageUrl(newArtifact.getImageUrl());
                    return artifactRepository.save(artifact);
                })
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    }
}
