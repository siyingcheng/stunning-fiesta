package com.simon.stunningfiesta.wizard;

import com.simon.stunningfiesta.system.Result;
import com.simon.stunningfiesta.system.StatusCode;
import com.simon.stunningfiesta.wizard.converters.WizardDtoToWizardConverter;
import com.simon.stunningfiesta.wizard.converters.WizardToWizardDtoConverter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {
    private final WizardService wizardService;

    private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

    private final WizardDtoToWizardConverter wizardDtoToWizardConverter;

    public WizardController(WizardService wizardService,
                            WizardToWizardDtoConverter wizardToWizardDtoConverter,
                            WizardDtoToWizardConverter wizardDtoToWizardConverter) {
        this.wizardService = wizardService;
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
        this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
    }

    @GetMapping
    public Result findAllWizards() {
        List<WizardDto> wizards = wizardService.findAll()
                .stream()
                .map(wizardToWizardDtoConverter::convert)
                .collect(Collectors.toList());
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Find All Success")
                .withData(wizards);
    }

    @GetMapping("/{wizardId}")
    public Result findWizardById(@PathVariable Integer wizardId) {
        Wizard wizard = wizardService.findById(wizardId);
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Find Wizard Success")
                .withData(wizardToWizardDtoConverter.convert(wizard));
    }

    @PostMapping
    public Result addWizard(@Valid @RequestBody WizardDto wizardDto) {
        Wizard savedWizard = wizardService.save(wizardDtoToWizardConverter.convert(wizardDto));
        WizardDto savedWizardDto = wizardToWizardDtoConverter.convert(savedWizard);
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Add Wizard Success")
                .withData(savedWizardDto);
    }

    @PutMapping("/{wizardId}")
    public Result updateWizardById(@PathVariable Integer wizardId,
                                   @Valid @RequestBody WizardDto wizardDto) {
        Wizard updatedWizard = wizardService.update(wizardId, wizardDtoToWizardConverter.convert(wizardDto));
        WizardDto updatedWizardDto = wizardToWizardDtoConverter.convert(updatedWizard);
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Update Wizard Success")
                .withData(updatedWizardDto);
    }

    @DeleteMapping("/{wizardId}")
    public Result deleteWizardById(@PathVariable Integer wizardId) {
        wizardService.deleteById(wizardId);
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Delete Wizard Success");
    }
}
