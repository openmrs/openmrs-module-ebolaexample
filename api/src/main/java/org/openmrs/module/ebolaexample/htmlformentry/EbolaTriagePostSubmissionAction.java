package org.openmrs.module.ebolaexample.htmlformentry;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaDemoData;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.HtmlFormEntryUtil;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Date;

public class EbolaTriagePostSubmissionAction implements CustomFormSubmissionAction {

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        if (!formEntrySession.getContext().getMode().equals(FormEntryContext.Mode.ENTER)) {
            return;
        }
        Concept admitQuestion = HtmlFormEntryUtil.getConcept("CIEL:162620");
        Concept yes = HtmlFormEntryUtil.getConcept("CIEL:1065");
        Concept no = HtmlFormEntryUtil.getConcept("CIEL:1066");

        doApplyAction(formEntrySession.getSubmissionActions().getEncountersToCreate().get(0),
                admitQuestion, yes, no,
                Context.getService(AdtService.class),
                MetadataUtils.existing(Location.class, EbolaDemoData._Location.ASSESSMENT),
                MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM),
                Context.getProgramWorkflowService());
    }

    void doApplyAction(Encounter encounter, Concept admitQuestion, Concept yes, Concept no,
                       AdtService adtService, Location assessment, Program ebolaProgram,
                       ProgramWorkflowService programWorkflowService) {

        adtService.ensureActiveVisit(encounter.getPatient(), assessment);

        Boolean shouldAdmit = shouldAdmit(encounter, admitQuestion, yes, no);
        if (shouldAdmit == null) {
            return;
        }
        else if (shouldAdmit) {
            admit(adtService, encounter, assessment);
            enroll(programWorkflowService, encounter.getPatient(), ebolaProgram, encounter.getEncounterDatetime());
        }
        else {
            // send the patient home => close the active visit
            closeVisit(adtService, encounter.getPatient(), encounter.getLocation());
        }
    }

    private void enroll(ProgramWorkflowService service, Patient patient, Program ebolaProgram, Date enrollmentDate) {
        for (PatientProgram candidate : service.getPatientPrograms(patient, ebolaProgram, null, null, null, null, false)) {
            if (candidate.getActive()) {
                return;
            }
        }
        PatientProgram enrollment = new PatientProgram();
        enrollment.setProgram(ebolaProgram);
        enrollment.setPatient(patient);
        enrollment.setDateEnrolled(enrollmentDate);
        service.savePatientProgram(enrollment);
    }

    private void admit(AdtService adtService, Encounter encounter, Location toLocation) {
        VisitDomainWrapper activeVisit = adtService.getActiveVisit(encounter.getPatient(), encounter.getLocation());
        AdtAction adtAction = new AdtAction(activeVisit.getVisit(), toLocation, encounter.getProvidersByRoles(), AdtAction.Type.ADMISSION);
        adtService.createAdtEncounterFor(adtAction);
    }

    private void closeVisit(AdtService adtService, Patient patient, Location encounterLocation) {
        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, encounterLocation);
        if (activeVisit != null) {
            adtService.closeAndSaveVisit(activeVisit.getVisit());
        }
    }

    private Boolean shouldAdmit(Encounter encounter, Concept admitQuestion, Concept yes, Concept no) {
        Obs obs = getObs(encounter, admitQuestion);
        if (obs != null) {
            if (obs.getValueCoded().equals(yes)) {
                return true;
            } else if (obs.getValueCoded().equals(no)) {
                return false;
            }
        }
        return null;
    }

    private Obs getObs(Encounter encounter, Concept concept) {
        for (Obs candidate : encounter.getObs()) {
            if (candidate.getConcept().equals(concept)) {
                return candidate;
            }
        }
        return null;
    }

}
