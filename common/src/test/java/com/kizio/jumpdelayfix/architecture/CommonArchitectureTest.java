package com.kizio.jumpdelayfix.architecture;

import com.kizio.jumpdelayfix.state.RuntimeState;
import com.tngtech.archunit.base.DescribedPredicate;
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

    @Test
    void coreDomainPackagesMustNotCallStaticMethodsOnRuntimeState() {
        ArchRule rule = noClasses()
                .that()
                .resideInAnyPackage("..jump..", "..engine..", "..config..")
                .and()
                .haveSimpleNameNotEndingWith("Test")
                .should()
                .callMethodWhere(DescribedPredicate.describe(
                        "calls static method on RuntimeState",
                        call -> call.getTargetOwner().isEquivalentTo(RuntimeState.class)
                                && (call.getTarget().getName().equals("isEnabled")
                                        || call.getTarget().getName().equals("getProfile")
                                        || call.getTarget().getName().equals("toggle")
                                        || call.getTarget().getName().equals("setEnabled")
                                        || call.getTarget().getName().equals("setProfile")
                                        || call.getTarget().getName().equals("cycleProfile")
                                        || call.getTarget().getName().equals("reset"))))
                .because("Core domain components must use injected RuntimeState instance instead of static methods");

        rule.check(COMMON_CLASSES);
    }
}
