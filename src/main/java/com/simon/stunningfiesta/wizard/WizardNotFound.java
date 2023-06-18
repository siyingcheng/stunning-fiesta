package com.simon.stunningfiesta.wizard;

public class WizardNotFound extends RuntimeException {
    public WizardNotFound(Integer id) {
        super("Could not find wizard with Id " + id + " :(");
    }
}
