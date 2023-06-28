package com.simon.stunningfiesta.wizard;

import com.simon.stunningfiesta.artifact.Artifact;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wizard implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "owner")
    private List<Artifact> artifacts = new ArrayList<>();

    public Wizard() {
    }

    public void removeArtifact(Artifact artifactToBeRemoved) {
        this.artifacts.remove(artifactToBeRemoved);
        artifactToBeRemoved.setOwner(null);
    }

    public Integer getNumberOfArtifacts() {
        return this.artifacts.size();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public Wizard withId(Integer id) {
        this.id = id;
        return this;
    }

    public Wizard withName(String name) {
        this.name = name;
        return this;
    }

    public Wizard withArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
        return this;
    }

    public Wizard addArtifacts(Artifact... artifacts) {
        for (Artifact artifact : artifacts) {
            artifact.setOwner(this);
            this.artifacts.add(artifact);
        }
        return this;
    }
}
