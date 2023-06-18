package com.simon.stunningfiesta.wizard.converters;

import com.simon.stunningfiesta.wizard.Wizard;
import com.simon.stunningfiesta.wizard.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardDtoToWizardConverter implements Converter<WizardDto, Wizard> {
    @Override
    public Wizard convert(WizardDto source) {
        return new Wizard()
                .withId(source.id())
                .withName(source.name());
    }
}
