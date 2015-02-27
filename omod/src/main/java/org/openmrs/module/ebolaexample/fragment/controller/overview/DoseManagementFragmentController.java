package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public class DoseManagementFragmentController {

    public void delete(@RequestParam(value="scheduledDoseId", required = true) String scheduledDoseId,
                       @RequestParam(value="dateVoided", required = true) Date dateVoided,
                       @RequestParam(value="voidedBy", required = true) String voidedBy,
                       @SpringBean PharmacyService pharmacyService) {

        System.out.println("HELLO WORLD");


    }
}
