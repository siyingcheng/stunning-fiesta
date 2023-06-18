package com.simon.stunningfiesta.artifact.converts;

import com.simon.stunningfiesta.artifact.Artifact;
import com.simon.stunningfiesta.artifact.ArtifactDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDto, Artifact> {
    @Override
    public Artifact convert(ArtifactDto source) {
        return new Artifact()
                .withId(source.id())
                .withName(source.name())
                .withDescription(source.description())
                .withImageUrl(source.imageUrl());
    }
}
