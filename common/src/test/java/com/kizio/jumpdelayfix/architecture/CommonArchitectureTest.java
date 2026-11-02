package com.kizio.jumpdelayfix.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class CommonArchitectureTest {

    private static final JavaClasses COMMON_CLASSES =
            new ClassFileImporter().importPackages("com.kizio.jumpdelayfix");

    @Test
    void commonMustNotDependOnFabricOrNeoForgeLoaders() {
        ArchRule rule = noClasses()
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("net.fabricmc..", "net.neoforged..")
                .because("Common module must remain strictly loader-agnostic");

        rule.check(COMMON_CLASSES);
    }
}
