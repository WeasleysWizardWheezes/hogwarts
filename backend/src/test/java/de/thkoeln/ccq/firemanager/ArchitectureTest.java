package de.thkoeln.ccq.firemanager;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@Tag("unit")
@AnalyzeClasses(
        packages = "de.thkoeln.ccq.firemanager",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    // ═══════════════════════════════════════════════
    // Layer-Zugriff: Controller → Service (NICHT Repository)
    // ═══════════════════════════════════════════════

    @ArchTest
    static final ArchRule controllers_should_not_access_repositories =
            noClasses().that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().areAssignableTo(JpaRepository.class)
                    .because("Controller dürfen nicht direkt auf Repositories zugreifen – nur über Services");

    // ═══════════════════════════════════════════════
    // Layer-Zugriff: Repository → KEIN Service oder Controller
    // ═══════════════════════════════════════════════

    @ArchTest
    static final ArchRule repositories_should_not_access_services =
            noClasses().that().areAssignableTo(JpaRepository.class)
                    .should().dependOnClassesThat().areAnnotatedWith(Service.class)
                    .because("Repositories dürfen nicht auf Services zugreifen");

    @ArchTest
    static final ArchRule repositories_should_not_access_controllers =
            noClasses().that().areAssignableTo(JpaRepository.class)
                    .should().dependOnClassesThat().areAnnotatedWith(RestController.class)
                    .because("Repositories dürfen nicht auf Controller zugreifen");

    // ═══════════════════════════════════════════════
    // Layer-Zugriff: Service → KEIN Controller
    // ═══════════════════════════════════════════════

    @ArchTest
    static final ArchRule services_should_not_access_controllers =
            noClasses().that().areAnnotatedWith(Service.class)
                    .should().dependOnClassesThat().areAnnotatedWith(RestController.class)
                    .because("Services dürfen nicht auf Controller zugreifen");

    // ═══════════════════════════════════════════════
    // Naming-Konventionen
    // ═══════════════════════════════════════════════

    @ArchTest
    static final ArchRule controllers_should_be_suffixed =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .because("Controller-Klassen müssen mit 'Controller' enden");

    @ArchTest
    static final ArchRule services_should_be_suffixed =
            classes().that().areAnnotatedWith(Service.class)
                    .should().haveSimpleNameEndingWith("Service")
                    .because("Service-Klassen müssen mit 'Service' enden");
}
