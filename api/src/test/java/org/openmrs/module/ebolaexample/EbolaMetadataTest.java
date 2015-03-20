package org.openmrs.module.ebolaexample;

import org.junit.Before;
import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatasharing.api.MetadataSharingService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Locale;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;

/**
 * Sets up metadata that we expect to be set up as a result of other reference application modules
 */
public abstract class EbolaMetadataTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private LocationService locationService;

    @Autowired
    VisitService visitService;

    @Autowired
    EncounterService encounterService;

    @Autowired
    PersonService personService;

    @Autowired @Qualifier("adminService")
    AdministrationService administrationService;

    @Before
    public void setUp() throws Exception {
        createExpectedCielConcepts();
        createExpectedLocationTags();
        createExpectedVisitTypes();
        createExpectedEncounterTypes();
        createExpectedPersonAttributeTypes();
    }

    private void createExpectedPersonAttributeTypes() {
        PersonAttributeType phoneAttributeType = new PersonAttributeType();
        phoneAttributeType.setUuid(EbolaMetadata._PersonAttributeType.TELEPHONE_NUMBER);
        phoneAttributeType.setName("Telephone Number");
        phoneAttributeType.setDescription("The telephone number for the person");
        personService.savePersonAttributeType(phoneAttributeType);
    }

    private void createExpectedLocationTags() {
        locationService.saveLocationTag(new LocationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION, "description"));
        locationService.saveLocationTag(new LocationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER, "description"));

        LocationTag supportsLogin = new LocationTag(AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN, "description");
        supportsLogin.setUuid(AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID);
        locationService.saveLocationTag(supportsLogin);
    }

    private void createExpectedCielConcepts() {
        Concept ebolaProgram = new Concept();
        ebolaProgram.setUuid(EbolaMetadata._Concept.EBOLA_PROGRAM);
        ebolaProgram.addName(new ConceptName("Ebola Program", Locale.ENGLISH));
        ebolaProgram.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
        ebolaProgram.setConceptClass(conceptService.getConceptClassByName("Misc"));

        Concept weightInKg = new Concept();
        weightInKg.setUuid(EbolaMetadata._Concept.WEIGHT_IN_KG);
        weightInKg.addName(new ConceptName("Weight In Kg", Locale.ENGLISH));
        weightInKg.setDatatype(conceptService.getConceptDatatypeByName("Numeric"));
        weightInKg.setConceptClass(conceptService.getConceptClassByName("Misc"));

        Concept typeOfPatient = new Concept();
        typeOfPatient.setUuid(EbolaMetadata._Concept.TYPE_OF_PATIENT);
        typeOfPatient.addName(new ConceptName("Type of Patient", Locale.ENGLISH));
        typeOfPatient.setDatatype(conceptService.getConceptDatatypeByName("Coded"));
        typeOfPatient.setConceptClass(conceptService.getConceptClassByName("Misc"));

        Concept ebolaStage = new Concept();
        ebolaStage.setUuid(EbolaMetadata._Concept.EBOLA_STAGE);
        ebolaStage.addName(new ConceptName("Ebola Stage", Locale.ENGLISH));
        ebolaStage.setDatatype(conceptService.getConceptDatatypeByName("Coded"));
        ebolaStage.setConceptClass(conceptService.getConceptClassByName("Misc"));

        conceptService.saveConcept(ebolaStage);
        conceptService.saveConcept(ebolaProgram);
        conceptService.saveConcept(weightInKg);
        conceptService.saveConcept(typeOfPatient);
    }

    private void createExpectedVisitTypes() {
        String FACILITY_VISIT_TYPE_UUID = "7b0f5697-27e3-40c4-8bae-f4049abfb4ed"; // from referencemetadata module
        VisitType visitType = new VisitType("Hospital or Clinic Visit", "Visit at hospital or clinic");
        visitType.setUuid(FACILITY_VISIT_TYPE_UUID);

        visitService.saveVisitType(visitType);
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, visitType.getUuid()));
    }

    private void createExpectedEncounterTypes() {
        // from referencemetadata module
        String ADMISSION_ENCOUNTER_TYPE_UUID = "e22e39fd-7db2-45e7-80f1-60fa0d5a4378";
        String DISCHARGE_ENCOUNTER_TYPE_UUID = "181820aa-88c9-479b-9077-af92f5364329";
        String TRANSFER_ENCOUNTER_TYPE_UUID = "7b68d557-85ef-4fc8-b767-4fa4f5eb5c23";

        encounterService.saveEncounterType(encounterType("Admission", "Admit to inpatient", ADMISSION_ENCOUNTER_TYPE_UUID));
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE, ADMISSION_ENCOUNTER_TYPE_UUID));

        encounterService.saveEncounterType(encounterType("Exit from Inpatient", "Exit from inpatient", DISCHARGE_ENCOUNTER_TYPE_UUID));
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_EXIT_FROM_INPATIENT_ENCOUNTER_TYPE, DISCHARGE_ENCOUNTER_TYPE_UUID));

        encounterService.saveEncounterType(encounterType("Transfer", "Transfer within hospital", TRANSFER_ENCOUNTER_TYPE_UUID));
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, TRANSFER_ENCOUNTER_TYPE_UUID));

    }
}
