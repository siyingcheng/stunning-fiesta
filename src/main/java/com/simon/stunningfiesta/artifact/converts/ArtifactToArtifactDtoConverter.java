package com.simon.stunningfiesta.artifact.converts;

import com.simon.stunningfiesta.artifact.Artifact;
import com.simon.stunningfiesta.artifact.ArtifactDto;
import com.simon.stunningfiesta.wizard.converters.WizardToWizardDtoConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {
    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardToWizardDtoConverter) {
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
    }

    @Override
    public ArtifactDto convert(Artifact source) {
        return new ArtifactDto(source.getId(),
                source.getName(),
                source.getDescription(),
                source.getImageUrl(),
                source.getOwner() == null
                        ? null
                        : wizardToWizardDtoConverter.convert(source.getOwner()));
    }
}
