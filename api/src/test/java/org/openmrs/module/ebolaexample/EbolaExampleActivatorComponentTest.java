package org.openmrs.module.ebolaexample;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EbolaExampleActivatorComponentTest extends EbolaMetadataTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private LocationService locationService;

    @Test
    public void testStarted() throws Exception {
        new EbolaExampleActivator().started();
    }

    @Test
    public void setPreferredConceptName_shouldSetName() throws Exception {
        String uuid = "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
        Concept weight = conceptService.getConceptByUuid(uuid);
        weight.addName(new ConceptName("Weight", Locale.ENGLISH));
        conceptService.saveConcept(weight);
        assertThat(conceptService.getConceptByUuid(uuid).getPreferredName(Locale.ENGLISH).getName(), is("WEIGHT (KG)"));

        new EbolaExampleActivator().setPreferredConceptName(conceptService, uuid, "Weight");
        assertThat(conceptService.getConceptByUuid(uuid).getPreferredName(Locale.ENGLISH).getName(), is("Weight"));
    }

}