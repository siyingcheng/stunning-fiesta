package com.simon.stunningfiesta.wizard;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WizardService {
    private final WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
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
                .orElseThrow(() -> new WizardNotFound(id));
    }

    public Wizard update(Integer id, Wizard newWizard) {
        return wizardRepository.findById(id)
                .map(wizard -> {
                    wizard.setName(newWizard.getName());
                    return wizardRepository.save(wizard);
                })
                .orElseThrow(() -> new WizardNotFound(id));
    }
}
