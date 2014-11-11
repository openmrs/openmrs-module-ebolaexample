package org.openmrs.module.ebolaexample.page.controller.inpatientfollowup;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public class IndexPageController {

    public void get(@RequestParam("patient") Patient patient,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    UiSessionContext sessionContext,
                    PageModel model) {

        patientDomainWrapper.setPatient(patient);
        VisitDomainWrapper activeVisit = patientDomainWrapper.getActiveVisit(sessionContext.getSessionLocation());
        Location assignedLocation = activeVisit.getInpatientLocation(new Date());

        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("visit", activeVisit);
        model.addAttribute("assignedLocation", assignedLocation);
        model.addAttribute("provider", sessionContext.getCurrentProvider());
    }

}
