package org.openmrs.module.ebolaexample;

import org.junit.Test;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.springframework.beans.factory.annotation.Autowired;

public class EbolaExampleActivatorComponentTest extends EbolaMetadataTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private LocationService locationService;

    @Test
    public void testStarted() throws Exception {
        new EbolaExampleActivator().started();
    }

}