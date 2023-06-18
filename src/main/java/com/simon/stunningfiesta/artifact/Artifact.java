package com.simon.stunningfiesta.artifact;

import com.simon.stunningfiesta.wizard.Wizard;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Entity
public class Artifact implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;

    @ManyToOne
    private Wizard owner;

    public Artifact() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOwner(Wizard owner) {
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Wizard getOwner() {
        return owner;
    }

    public Artifact withId(Integer id) {
        this.id = id;
        return this;
    }

    public Artifact withName(String name) {
        this.name = name;
        return this;
    }

    public Artifact withDescription(String description) {
        this.description = description;
        return this;
    }

    public Artifact withImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Artifact withOwner(Wizard owner) {
        this.owner = owner;
        return this;
    }
}
