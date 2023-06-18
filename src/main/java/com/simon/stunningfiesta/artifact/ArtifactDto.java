package com.simon.stunningfiesta.artifact;

import com.simon.stunningfiesta.wizard.WizardDto;
import jakarta.validation.constraints.NotEmpty;

public record ArtifactDto(
        Integer id,
        @NotEmpty(message = "name is required")
        String name,
        @NotEmpty(message = "description is required")
        String description,
        @NotEmpty(message = "imageUrl is required")
        String imageUrl,
        WizardDto owner
) {
}
