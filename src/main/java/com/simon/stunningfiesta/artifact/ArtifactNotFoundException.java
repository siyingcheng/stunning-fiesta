package com.simon.stunningfiesta.artifact;

public class ArtifactNotFoundException extends RuntimeException {
    public ArtifactNotFoundException(Integer id) {
        super("Could not find artifact with Id " + id + " :(");
    }
}
