package com.simon.stunningfiesta.system;

import com.simon.stunningfiesta.artifact.Artifact;
import com.simon.stunningfiesta.artifact.ArtifactRepository;
import com.simon.stunningfiesta.hogwartsuser.HogwartsUser;
import com.simon.stunningfiesta.hogwartsuser.UserService;
import com.simon.stunningfiesta.wizard.Wizard;
import com.simon.stunningfiesta.wizard.WizardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBInitializer implements CommandLineRunner {

    private final ArtifactRepository artifactRepository;

    private final WizardRepository wizardRepository;

    private final UserService userService;

    public DBInitializer(ArtifactRepository artifactRepository,
                         WizardRepository wizardRepository,
                         UserService userService) {
        this.artifactRepository = artifactRepository;
        this.wizardRepository = wizardRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        Artifact invisibilityCloak = new Artifact()
                .withName("Invisibility Cloak")
                .withDescription("An invisibility cloak is used to make the wearer invisible")
                .withImageUrl("ImageUrl");
        Artifact deliminator = new Artifact()
                .withName("Deliminator")
                .withDescription("An deliminator is a device invented by Albums Dumbledore than ...")
                .withImageUrl("ImageUrl");
        Artifact elderWand = new Artifact()
                .withName("Elder Wand")
                .withDescription("The Elder Wand, known throughout history as ...")
                .withImageUrl("ImageUrl");
        Artifact polymorph = new Artifact()
                .withName("Polymorph")
                .withDescription("The polymorph could turn the target to some animal ...")
                .withImageUrl("ImageUrl");
        Artifact light = new Artifact()
                .withName("Light")
                .withDescription("The light artifact could make around fill lights ...")
                .withImageUrl("ImageUrl");

        Wizard albusDumledore = new Wizard()
                .withName("Albus Dumledore")
                .addArtifacts(elderWand, polymorph);
        Wizard harryPotter = new Wizard()
                .withName("Harry Potter")
                .addArtifacts(invisibilityCloak);
        Wizard nevilleLongbottom = new Wizard()
                .withName("Neville Longbottom")
                .addArtifacts(deliminator);

        wizardRepository.save(albusDumledore);
        wizardRepository.save(harryPotter);
        wizardRepository.save(nevilleLongbottom);

        artifactRepository.save(light);

        HogwartsUser manager = new HogwartsUser()
                .setUsername("simon")
                .setPassword("123456")
                .setEnabled(true)
                .setRoles("admin user");
        HogwartsUser user1 = new HogwartsUser()
                .setUsername("user1")
                .setPassword("123456")
                .setEnabled(true)
                .setRoles("user");
        HogwartsUser user2 = new HogwartsUser()
                .setUsername("user2")
                .setPassword("123456")
                .setEnabled(false)
                .setRoles("user");

        userService.save(manager);
        userService.save(user1);
        userService.save(user2);
    }
}
