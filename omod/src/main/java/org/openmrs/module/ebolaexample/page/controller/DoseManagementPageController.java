package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.pharmacy.DoseHistory;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public class DoseManagementPageController {

    public void get(@RequestParam("patient") Patient patient,
                    @RequestParam(value = "toDate", required = false) Date toDate,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    @SpringBean AdtService adtService,
                    @SpringBean BedAssignmentService bedAssignmentService,
                    @SpringBean PharmacyService pharmacyService,
                    UiSessionContext sessionContext,
                    PageModel model) {

        if (toDate == null) {
            toDate = DateUtil.getStartOfDay(new Date());
        }
        model.addAttribute("toDate", toDate);

        patientDomainWrapper.setPatient(patient);

        VisitDomainWrapper activeVisit = getActiveVisit(patient, adtService, sessionContext);

        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("activeVisit", activeVisit);
        model.addAttribute("wardAndBed", activeVisit == null ? null : bedAssignmentService.getAssignedWardAndBedFor(activeVisit.getVisit()));

        AppContextModel contextModel = sessionContext.generateAppContextModel();
        contextModel.put("patient", new PatientContextModel(patient));
        contextModel.put("visit", activeVisit == null ? null : new VisitContextModel(activeVisit));
        model.addAttribute("appContextModel", contextModel);

        Date fromDate = DateUtil.getStartOfDay(DateUtil.adjustDate(toDate, -6, DurationUnit.DAYS));
        DoseHistory doseHistory = pharmacyService.getScheduledDosesByPatientAndDateRange(patientDomainWrapper.getPatient(), fromDate, toDate);
        model.addAttribute("doseHistory", doseHistory);
    }

    private VisitDomainWrapper getActiveVisit(Patient patient, AdtService adtService, UiSessionContext sessionContext) {
        Location visitLocation = null;
        try {
            visitLocation = adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
        }
        catch (IllegalArgumentException ex) {
            // location does not support visits
        }
        return visitLocation == null ? null : adtService.getActiveVisit(patient, visitLocation);
    }
}
