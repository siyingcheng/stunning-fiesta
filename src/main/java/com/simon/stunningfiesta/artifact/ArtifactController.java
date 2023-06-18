package com.simon.stunningfiesta.artifact;

import com.simon.stunningfiesta.artifact.converts.ArtifactDtoToArtifactConverter;
import com.simon.stunningfiesta.artifact.converts.ArtifactToArtifactDtoConverter;
import com.simon.stunningfiesta.system.Result;
import com.simon.stunningfiesta.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactController {
    private final ArtifactService artifactService;

    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    public ArtifactController(ArtifactService artifactService,
                              ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
                              ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    }

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable Integer artifactId) {
        Artifact foundArtifact = artifactService.findById(artifactId);
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Find One Success")
                .withData(artifactToArtifactDtoConverter.convert(foundArtifact));
    }

    @GetMapping
    public Result findAllArtifacts() {
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Find All Success")
                .withData(artifactService
                        .findAll()
                        .stream()
                        .map(artifactToArtifactDtoConverter::convert)
                        .collect(Collectors.toList()));
    }

    @PostMapping
    public Result saveArtifact(@Valid @RequestBody ArtifactDto artifactDto) {
        Artifact savedArtifact = artifactService.save(artifactDtoToArtifactConverter.convert(artifactDto));
        ArtifactDto savedArtifactDto = artifactToArtifactDtoConverter.convert(savedArtifact);
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Add Success")
                .withData(savedArtifactDto);
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifactById(@PathVariable Integer artifactId,
                                     @Valid @RequestBody ArtifactDto artifactDto) {
        Artifact updatedArtifact = artifactService.update(artifactId, artifactDtoToArtifactConverter.convert(artifactDto));
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Update Success")
                .withData(artifactToArtifactDtoConverter.convert(updatedArtifact));
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifactById(@PathVariable Integer artifactId) {
        artifactService.deleteById(artifactId);
        return Result.of(true)
                .withCode(StatusCode.SUCCESS)
                .withMessage("Delete Success");
    }
}
