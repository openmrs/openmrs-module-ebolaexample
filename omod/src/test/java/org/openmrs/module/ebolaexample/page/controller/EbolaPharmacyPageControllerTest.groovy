package org.openmrs.module.ebolaexample.page.controller

import org.junit.Test
import org.openmrs.api.PatientService
import org.openmrs.ui.framework.page.PageModel
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class EbolaPharmacyPageControllerTest extends BaseModuleWebContextSensitiveTest {

    @Test
    public void shouldReturnModelAttributes() throws Exception {
        EbolaPharmacyPageController controller =
                (EbolaPharmacyPageController) applicationContext.getBean("ebolaPharmacyPageController");
        PatientService patientService = (PatientService) applicationContext.getBean("patientService");
        PageModel mav = new PageModel();
        controller.get(patientService, mav);
        assertNotNull(mav);
        assertTrue(mav.containsKey("ebolaPatients"))
    }

}
