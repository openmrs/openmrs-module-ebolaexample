package org.openmrs.module.ebolaexample.page.controller;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EbolaPharmacyPageControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private LocationService locationService;

    @Test
    public void shouldReturnModelAttributes() throws Exception {
        EbolaPharmacyPageController controller = (EbolaPharmacyPageController) applicationContext.getBean("ebolaPharmacyPageController");
        PatientService patientService = (PatientService) applicationContext.getBean("patientService");
        PageModel mav = new PageModel();
        controller.get(patientService, null, mav);
        assertTrue(mav.containsKey("ebolaPatients"));
        assertTrue(mav.containsKey("selectedWard"));
        assertTrue(mav.containsKey("today"));
        assertTrue(mav.containsKey("wards"));
    }

    @Test
    public void shouldReturnCorrectModelAttributesForParticularWard() throws Exception {
        //Uuid for location "Never Never Land" is 167ce20c-4785-4285-9119-d197268f7f4a
        Location ward = locationService.getLocationByUuid("167ce20c-4785-4285-9119-d197268f7f4a");

        EbolaPharmacyPageController controller = (EbolaPharmacyPageController) applicationContext.getBean("ebolaPharmacyPageController");
        PatientService patientService = (PatientService) applicationContext.getBean("patientService");
        PageModel mav = new PageModel();
        controller.get(patientService, ward.getUuid(), mav);
        assertEquals(mav.getAttribute("selectedWard"), ward);
    }

}
