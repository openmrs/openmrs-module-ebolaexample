package org.openmrs.module.ebolaexample;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.LocationTag;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class EbolaExampleActivatorComponentTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private LocationService locationService;

    @Test
    public void testStarted() throws Exception {
        createExpectedCielConcepts();
        createExpectedLocationTags();

        new EbolaExampleActivator().started();
    }

    private void createExpectedLocationTags() {
        locationService.saveLocationTag(new LocationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION, "description"));
        locationService.saveLocationTag(new LocationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER, "description"));

        LocationTag supportsLogin = new LocationTag(AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN, "description");
        supportsLogin.setUuid(AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID);
        locationService.saveLocationTag(supportsLogin);
    }

    private void createExpectedCielConcepts() {
        Concept concept = new Concept();
        concept.setUuid("162637AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        concept.addName(new ConceptName("Ebola Program", Locale.ENGLISH));
        concept.setDatatype(conceptService.getConceptDatatypeByName("N/A"));
        concept.setConceptClass(conceptService.getConceptClassByName("Misc"));

        conceptService.saveConcept(concept);
    }

}