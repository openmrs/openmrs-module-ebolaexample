package org.openmrs.module.ebolaexample.page.controller;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActivePatientsPageControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private BedAssignmentService bedAssignmentService;

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
    }

    @Test
    public void shouldReturnModelAttributes() throws Exception {
        ActivePatientsPageController controller = (ActivePatientsPageController) applicationContext.getBean("activePatientsPageController");
        PageModel mav = new PageModel();
        controller.get(bedAssignmentService, mav);
        assertTrue(mav.containsKey("assignments"));
        assertTrue(mav.containsKey("today"));
    }

}
