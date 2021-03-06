package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClinicalNotesPageController {

    public void get(@RequestParam("patient") Patient patient,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    @SpringBean AdtService adtService,
                    @SpringBean BedAssignmentService bedAssignmentService,
                    UiSessionContext sessionContext,
                    PageModel model) {

        patientDomainWrapper.setPatient(patient);

        VisitDomainWrapper activeVisit = getActiveVisit(patient, adtService, sessionContext);

        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("activeVisit", activeVisit);
        model.addAttribute("wardAndBed", activeVisit == null ? null : bedAssignmentService.getAssignedWardAndBedFor(activeVisit.getVisit()));

        AppContextModel contextModel = sessionContext.generateAppContextModel();
        contextModel.put("patient", new PatientContextModel(patient));
        contextModel.put("visit", activeVisit == null ? null : new VisitContextModel(activeVisit));
        model.addAttribute("appContextModel", contextModel);
        model.addAttribute("clinicalNotes", getClinicalNotes(patient));
    }

    private List<Encounter> getClinicalNotes(Patient patient) {
        EncounterService encounterService = Context.getEncounterService();
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);
        List<Form> forms = Arrays.asList(Context.getFormService().getFormByUuid(EbolaMetadata._Form.INPATIENT_OBSERVATIONS_AND_TREATMENT));
        List<Encounter> encounters = encounterService.getEncounters(patient, null, null, null, forms, Arrays.asList(encounterType), false);
        Collections.reverse(encounters);
        return encounters;
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
