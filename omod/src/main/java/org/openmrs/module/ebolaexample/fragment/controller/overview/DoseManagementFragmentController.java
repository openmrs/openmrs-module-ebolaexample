package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public class DoseManagementFragmentController {

    public void delete(@RequestParam(value = "scheduledDoseUuid", required = true) String scheduledDoseUuid,
                       @SpringBean PharmacyService pharmacyService) {

        ScheduledDose scheduledDose = pharmacyService.getScheduledDoseByUuid(scheduledDoseUuid);

        scheduledDose.setVoided(true);
        scheduledDose.setDateVoided(new Date());
        scheduledDose.setVoidedBy(Context.getAuthenticatedUser());

        pharmacyService.saveScheduledDose(scheduledDose);
    }
}
