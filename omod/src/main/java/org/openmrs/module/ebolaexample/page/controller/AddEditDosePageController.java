package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.FormatUtil;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.ValidateUtil;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

public class AddEditDosePageController {

    public void get(@RequestParam("patient") Patient patient,
                    @RequestParam("prescription") Order prescription,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    @SpringBean AdtService adtService,
                    @SpringBean BedAssignmentService bedAssignmentService,
                    @SpringBean PharmacyService pharmacyService,
                    UiSessionContext sessionContext,
                    PageModel model) {

        patientDomainWrapper.setPatient(patient);
        VisitDomainWrapper activeVisit = getActiveVisit(patient, adtService, sessionContext);

        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("activeVisit", activeVisit);
        model.addAttribute("wardAndBed", activeVisit == null ? null : bedAssignmentService.getAssignedWardAndBedFor(activeVisit.getVisit()));

        model.addAttribute("prescription", prescription);
        model.addAttribute("formatter", new FormatUtil());
    }

    public String post(@BindParams ScheduledDose dose,
                       Errors errors,
                       @SpringBean PharmacyService pharmacyService,
                       UiUtils ui) {

        ValidateUtil.validate(dose, errors);
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Errors: " + OpenmrsUtil.join(errors.getAllErrors(), ", "));
        }

        pharmacyService.saveScheduledDose(dose);

        SimpleObject redirectParams = SimpleObject.create("patient", dose.getOrder().getPatient().getUuid());
        return "redirect:" + ui.pageLink("ebolaexample", "doseManagement", redirectParams);
    }

    private VisitDomainWrapper getActiveVisit(Patient patient, AdtService adtService, UiSessionContext sessionContext) {
        Location visitLocation = null;
        try {
            visitLocation = adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
        } catch (IllegalArgumentException ex) {
            // location does not support visits
        }
        return visitLocation == null ? null : adtService.getActiveVisit(patient, visitLocation);
    }

}
