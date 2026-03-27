package se.inera.intyg.intygmockservice;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.intygmockservice.domain.navigation.model.PersonId;
import se.inera.intyg.intygmockservice.infrastructure.repository.AbstractInMemoryRepository;

@AnalyzeClasses(packages = "se.inera.intyg.intygmockservice")
class ArchitectureTest {

  // A. Layer dependency rules

  @ArchTest
  static final ArchRule domain_must_not_depend_on_application =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..application..");

  @ArchTest
  static final ArchRule domain_must_not_depend_on_infrastructure =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..infrastructure..");

  // B. Controllers and Responders must not use domain types.

  @ArchTest
  static final ArchRule controllers_must_not_use_domain =
      noClasses()
          .that()
          .haveSimpleNameEndingWith("Controller")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..domain..");

  @ArchTest
  static final ArchRule responders_must_not_use_domain =
      noClasses()
          .that()
          .haveSimpleNameEndingWith("ResponderImpl")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..domain..");

  // C. Spring annotation conventions

  @ArchTest
  static final ArchRule domain_must_not_use_spring =
      noClasses()
          .that()
          .resideInAPackage("..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("org.springframework..");

  @ArchTest
  static final ArchRule controllers_in_application_must_be_rest_controllers =
      classes()
          .that()
          .haveSimpleNameEndingWith("Controller")
          .and()
          .resideInAPackage("..application..")
          .should()
          .beAnnotatedWith(RestController.class);

  @ArchTest
  static final ArchRule services_in_application_must_be_annotated_with_service =
      classes()
          .that()
          .haveSimpleNameEndingWith("Service")
          .and()
          .resideInAPackage("..application..")
          .should()
          .beAnnotatedWith(Service.class);

  @ArchTest
  static final ArchRule responders_must_be_annotated_with_service =
      classes()
          .that()
          .haveSimpleNameEndingWith("ResponderImpl")
          .should()
          .beAnnotatedWith(Service.class);

  @ArchTest
  static final ArchRule converters_in_application_must_be_component_or_service =
      classes()
          .that()
          .haveSimpleNameEndingWith("Converter")
          .and()
          .resideInAPackage("..application..")
          .should(
              new ArchCondition<JavaClass>("be annotated with @Component or @Service") {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                  final var annotated =
                      javaClass.isAnnotatedWith(Component.class)
                          || javaClass.isAnnotatedWith(Service.class);
                  if (!annotated) {
                    events.add(
                        SimpleConditionEvent.violated(
                            javaClass,
                            javaClass.getFullName()
                                + " is not annotated with @Component or @Service"));
                  }
                }
              });

  @ArchTest
  static final ArchRule repositories_in_infrastructure_must_be_annotated =
      classes()
          .that()
          .haveSimpleNameEndingWith("Repository")
          .and()
          .resideInAPackage("..infrastructure..")
          .and()
          .areTopLevelClasses()
          .and()
          .doNotHaveSimpleName("AbstractInMemoryRepository")
          .should()
          .beAnnotatedWith(Repository.class);

  // D. Feature package isolation.
  // common is cross-cutting: all feature packages may depend on it.
  // reset is an orchestrator: it intentionally aggregates all service features on system reset.

  @ArchTest
  static final ArchRule feature_packages_must_not_depend_on_each_other =
      slices()
          .matching("..application.(*)..")
          .should()
          .notDependOnEachOther()
          .ignoreDependency(alwaysTrue(), resideInAPackage("..application.common.."))
          .ignoreDependency(resideInAPackage("..application.reset.."), alwaysTrue());

  // E. SOAP Responders must reside in the Application layer

  @ArchTest
  static final ArchRule responders_must_reside_in_application =
      classes()
          .that()
          .haveSimpleNameEndingWith("ResponderImpl")
          .should()
          .resideInAPackage("..application..");

  // F. domain.behavior.model must not re-introduce dependencies on domain.behavior.service.

  @ArchTest
  static final ArchRule domain_behavior_model_must_not_depend_on_domain_behavior_service =
      noClasses()
          .that()
          .resideInAPackage("..domain.behavior.model..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..domain.behavior.service..");

  // G. Concrete repositories must extend AbstractInMemoryRepository, except
  // InMemoryBehaviorRuleRepository which uses ConcurrentHashMap directly.

  @ArchTest
  static final ArchRule repositories_must_extend_abstract_or_be_behavior_rule_repository =
      classes()
          .that()
          .haveSimpleNameEndingWith("Repository")
          .and()
          .resideInAPackage("..infrastructure..")
          .and()
          .areTopLevelClasses()
          .and()
          .doNotHaveSimpleName("AbstractInMemoryRepository")
          .and()
          .doNotHaveSimpleName("InMemoryBehaviorRuleRepository")
          .should()
          .beAssignableTo(AbstractInMemoryRepository.class);

  // H. Navigation model personId fields must use the PersonId value object.

  @ArchTest
  static final ArchRule navigation_model_person_id_fields_must_be_typed_as_person_id =
      fields()
          .that()
          .haveName("personId")
          .and()
          .areDeclaredInClassesThat()
          .resideInAPackage("..domain.navigation.model..")
          .should()
          .haveRawType(PersonId.class);
}
