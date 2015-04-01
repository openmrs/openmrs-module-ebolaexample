package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.FormatUtil;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

public class AddClinicalNotePageController {

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

        model.addAttribute("formatter", new FormatUtil());
    }

    public String post(@RequestParam("patient") Patient patient,
                       @RequestParam("note") String note,
                       Errors errors,
                       UiSessionContext sessionContext,
                       UiUtils ui) {


        Encounter encounter = new Encounter();
        Person person = Context.getAuthenticatedUser().getPerson();
        encounter.setProvider(person);
        encounter.setPatient(patient);
        EncounterService encounterService = Context.getEncounterService();
        encounter.setEncounterType(encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP));
        encounter.setForm(Context.getFormService().getFormByUuid(EbolaMetadata._Form.INPATIENT_OBSERVATIONS_AND_TREATMENT));
        encounter.setEncounterDatetime(new Date());
        encounter.setLocation(sessionContext.getSessionLocation());

        Concept concept = Context.getConceptService().getConcept(162169);
        Obs obs = new Obs(patient, concept, null, null);
        obs.setValueText(note);
        encounter.addObs(obs);
        encounterService.saveEncounter(encounter);

        SimpleObject redirectParams = SimpleObject.create("patient", patient.getUuid());
        return "redirect:" + ui.pageLink("ebolaexample", "clinicalNotes", redirectParams);
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
