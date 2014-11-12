package org.openmrs.module.ebolaexample.htmlformentry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Visit;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EbolaTriagePostSubmissionActionTest {

    private EbolaTriagePostSubmissionAction ebolaTriagePostSubmissionAction;
    private Patient patient;
    private Visit activeVisit;
    private Encounter encounter;
    private Concept admitQuestion;
    private Concept yes;
    private Concept no;
    private AdtService adtService;
    private Location triage;
    private Location assessment;
    private Program ebolaProgram;
    private ProgramWorkflowService programWorkflowService;

    @Before
    public void setUp() throws Exception {
        ebolaTriagePostSubmissionAction = new EbolaTriagePostSubmissionAction();

        triage = new Location();
        assessment = new Location();

        patient = new Patient();

        encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setLocation(triage);

        activeVisit = new Visit();
        activeVisit.setPatient(patient);
        activeVisit.addEncounter(encounter);

        admitQuestion = new Concept();
        yes = new Concept();
        no = new Concept();

        ebolaProgram = new Program();

        adtService = mock(AdtService.class);
        when(adtService.getActiveVisit(patient, triage)).thenReturn(new VisitDomainWrapper(activeVisit));

        programWorkflowService = mock(ProgramWorkflowService.class);
    }

    @Test
    public void testSendHomeWhenUserSaysNo() throws Exception {
        Obs doNotAdmit = new Obs(patient, admitQuestion, new Date(), triage);
        doNotAdmit.setValueCoded(no);
        encounter.addObs(doNotAdmit);

        ebolaTriagePostSubmissionAction.doApplyAction(encounter, admitQuestion, yes, no, adtService, assessment, ebolaProgram, programWorkflowService);
        verify(adtService).closeAndSaveVisit(activeVisit);
    }

    @Test
    public void testAdmitWhenUserSaysYes() throws Exception {
        Obs admit = new Obs(patient, admitQuestion, new Date(), triage);
        admit.setValueCoded(yes);
        encounter.addObs(admit);

        ebolaTriagePostSubmissionAction.doApplyAction(encounter, admitQuestion, yes, no, adtService, assessment, ebolaProgram, programWorkflowService);
        verify(adtService).createAdtEncounterFor(argThat(admitsToAssessment()));
    }

    @Test
    public void testEnrollWhenUserSaysYes() throws Exception {
        Obs admit = new Obs(patient, admitQuestion, new Date(), triage);
        admit.setValueCoded(yes);
        encounter.addObs(admit);

        when(programWorkflowService.getPatientPrograms(patient, ebolaProgram, null, null, null, null, false)).thenReturn(Collections.<PatientProgram>emptyList());

        ebolaTriagePostSubmissionAction.doApplyAction(encounter, admitQuestion, yes, no, adtService, assessment, ebolaProgram, programWorkflowService);
        verify(programWorkflowService).savePatientProgram(argThat(enrollsPatientInEbolaProgram()));
    }

    @Test
    public void testDoesNotEnrollWhenAlreadyEnrolled() throws Exception {
        PatientProgram already = new PatientProgram();
        already.setPatient(patient);
        already.setProgram(ebolaProgram);
        already.setDateEnrolled(new Date());
        when(programWorkflowService.getPatientPrograms(patient, ebolaProgram, null, null, null, null, false))
                .thenReturn(Arrays.asList(already));

        Obs admit = new Obs(patient, admitQuestion, new Date(), triage);
        admit.setValueCoded(yes);
        encounter.addObs(admit);

        ebolaTriagePostSubmissionAction.doApplyAction(encounter, admitQuestion, yes, no, adtService, assessment, ebolaProgram, programWorkflowService);
        verify(programWorkflowService, never()).savePatientProgram(any(PatientProgram.class));
    }

    private ArgumentMatcher<PatientProgram> enrollsPatientInEbolaProgram() {
        return new ArgumentMatcher<PatientProgram>() {
            @Override
            public boolean matches(Object o) {
                PatientProgram actual = (PatientProgram) o;
                return actual.getPatient().equals(patient) && actual.getProgram().equals(ebolaProgram);
            }
        };
    }

    private ArgumentMatcher<AdtAction> admitsToAssessment() {
        return new ArgumentMatcher<AdtAction>() {
            @Override
            public boolean matches(Object o) {
                AdtAction actual = (AdtAction) o;
                return actual.getType().equals(AdtAction.Type.ADMISSION) &&
                        actual.getVisit().equals(activeVisit) &&
                        actual.getLocation().equals(assessment);
            }
        };
    }
}