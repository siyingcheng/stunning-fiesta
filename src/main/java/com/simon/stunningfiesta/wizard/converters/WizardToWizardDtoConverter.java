package com.simon.stunningfiesta.wizard.converters;

import com.simon.stunningfiesta.wizard.Wizard;
import com.simon.stunningfiesta.wizard.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {
    @Override
    public WizardDto convert(Wizard source) {
        return new WizardDto(
                source.getId(),
                source.getName(),
                source.getNumberOfArtifacts());
    }
}
