package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

@Component
public class EbolaTestBaseMetadata extends AbstractMetadataBundle {

    @Autowired
    private ConceptService conceptService;

    @Autowired @Qualifier("adminService")
    private AdministrationService administrationService;

    @Override
    public void install() throws Exception {
        VisitType visitType = visitType("Facility Visit", "", uuid());
        install(visitType);
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_AT_FACILITY_VISIT_TYPE, visitType.getUuid()));

        EncounterType transfer = encounterType("Transfer", "Transfer between wards", uuid());
        install(transfer);
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE, transfer.getUuid()));

        install(personAttributeType("Test Patient", "", Boolean.class, null, true, 0, EmrApiConstants.TEST_PATIENT_ATTRIBUTE_UUID));

        install(locationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION, "", uuid()));
        install(locationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER, "", uuid()));
        install(locationTag("Supports Login", "", AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID));

        // We cannot do install(concept(...)) because of: java.lang.UnsupportedOperationException: Concepts can only be fetched for now
        conceptService.saveConcept(concept("Ebola Program", "162637AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Program"));

        conceptService.saveConcept(concept("IV", "160242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Route"));
        conceptService.saveConcept(concept("IO Needle", "162624AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Route"));
        conceptService.saveConcept(concept("mL", "162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Units"));
        conceptService.saveConcept(concept("min", "1733AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Time Units"));
        conceptService.saveConcept(concept("hr", "1822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Time Units"));

        installLabTestData();
    }

    private void installLabTestData(){
        Concept concept = concept("MALARIAL SMEAR", "32AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Coded", "Test");
        Concept concept2 = concept("Ebola SMEAR", "1030AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Coded", "Test");
        Concept concept664 = concept("Neg", "664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Misc");
        Concept concept703 = concept("Pos", "703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Misc");

        concept.addAnswer(new ConceptAnswer(concept664));
        concept.addAnswer(new ConceptAnswer(concept703));

        concept2.addAnswer(new ConceptAnswer(concept664));
        concept2.addAnswer(new ConceptAnswer(concept703));

        conceptService.saveConcept(concept664);
        conceptService.saveConcept(concept703);
        conceptService.saveConcept(concept);
        conceptService.saveConcept(concept2);
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }

    private Concept concept(String name, String uuid, String datatype, String conceptClass) {
        Concept concept = new Concept();
        concept.addName(new ConceptName(name, Locale.ENGLISH));
        concept.setUuid(uuid);
        concept.setDatatype(conceptService.getConceptDatatypeByName(datatype));
        concept.setConceptClass(conceptService.getConceptClassByName(conceptClass));
        return concept;
    }

}
