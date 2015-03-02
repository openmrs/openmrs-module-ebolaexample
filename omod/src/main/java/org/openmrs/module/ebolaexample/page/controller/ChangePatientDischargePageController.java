package org.openmrs.module.ebolaexample.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.VisitService;
import org.openmrs.module.ebolaexample.DateUtil;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ChangePatientDischargePageController {

    public void get(@SpringBean("patientService") PatientService patientService,
                    @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                    @SpringBean("conceptService") ConceptService conceptService,
                    UiUtils ui,
                    @RequestParam(value = "patientUuid", required = false) String patientUuid, PageModel model) {
        preparePageModel(patientService, programWorkflowService, conceptService, ui, patientUuid, model);
    }

    private void preparePageModel(PatientService patientService, ProgramWorkflowService programWorkflowService,
                                  ConceptService conceptService, UiUtils ui, String patientUuid, PageModel model) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        model.put("patient", patient);
        PatientProgram patientProgram = getPatientProgram(patient, programWorkflowService);
        model.put("patientProgram", patientProgram);
        model.put("outComes", getOutComeOptions(conceptService));

        model.put("currentOutcome", patientProgram != null ? patientProgram.getOutcome() : null);
        model.put("today", DateUtil.getDateToday());
        model.put("defaultDate", patientProgram.getDateCompleted() != null ? patientProgram.getDateCompleted() : DateUtil.getDateToday());

        if (!model.containsKey("error")) {
            model.put("error", "");
        }

        if (!model.containsKey("success")) {
            model.put("success", "");
        }
    }

    public String post(@SpringBean("patientService") PatientService patientService,
                       @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                       @SpringBean("conceptService") ConceptService conceptService,
                       @SpringBean("visitService") VisitService visitService,
                       UiUtils ui,
                       @RequestParam(value = "patientUuid", required = false) String patientUuid,
                       @RequestParam(value = "outCome", required = false) String outcomeUuid,
                       @RequestParam(value = "dateCompleted", required = true) Date dateCompleted, PageModel model) {
        Patient patient = patientService.getPatientByUuid(patientUuid);
        try {
            Concept outcome = StringUtils.isEmpty(outcomeUuid) ? null : conceptService.getConceptByUuid(outcomeUuid);

            if (patient == null) {
                throw new Exception("Patient not retrieved from database");
            }
            PatientProgram patientProgram = getPatientProgram(patient, programWorkflowService);
            if (patientProgram == null) {
                model.put("error", "Patient program not found");
            }

            dateCompleted = getCompletedDate(dateCompleted, patientProgram);

            if(outcome!= null && (OutComeType.DiedOnWard.toString().equals(outcome.toString()) || OutComeType.DeadOnArrival.toString().equals(outcome.toString()))){
                SetPatientDead(patientService, dateCompleted, patient, outcome);
            }

            patientProgram.setOutcome(outcome);
            patientProgram.setDateCompleted(outcome == null ? null : dateCompleted);

            programWorkflowService.savePatientProgram(patientProgram);

            Visit patientVisit = getPatientVisit(patient, visitService);
            if (patientVisit != null) {
                patientVisit.setStopDatetime(outcome == null ? null : dateCompleted);
                visitService.saveVisit(patientVisit);
            }

            model.put("success", "Discharge information modified successfully");

        } catch (Exception e)   {
            System.out.println("Error: " + e.getMessage());
            model.put("error", e.getMessage());
            preparePageModel(patientService, programWorkflowService, conceptService, ui, patientUuid, model);
            return null;
        }

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("patient", patient);
        return "redirect:" + ui.pageLink("ebolaexample", "ebolaOverview", args);
    }

    private Date getCompletedDate(Date dateCompleted, PatientProgram patientProgram) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String dateCompletedStr = simpleDateFormat.format(dateCompleted);
        String dateEnrolledStr = simpleDateFormat.format(patientProgram.getDateEnrolled());
        if (dateCompletedStr.equalsIgnoreCase(dateEnrolledStr)) {
            dateCompleted = new Date();
        }
        return dateCompleted;
    }

    private void SetPatientDead(PatientService patientService, Date deathDate, Patient patient, Concept outcome) {
        patient.setDead(true);
        patient.setDeathDate(deathDate);
        patient.setCauseOfDeath(outcome);
        patientService.savePatient(patient);
    }

    public static PatientProgram getPatientProgram(Patient patient, ProgramWorkflowService programWorkflowService) {
        Program program = MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM);
        List<PatientProgram> enrollments = programWorkflowService.getPatientPrograms(patient, program, null, null, null, null, false);
        return enrollments.size() > 0 ? enrollments.get(0) : null;
    }

    public static Visit getPatientVisit(Patient patient, VisitService visitService) {
        List<Visit> visits = visitService.getVisitsByPatient(patient, true, false);
        return visits.size() > 0 ? visits.get(0) : null;
    }

    enum OutComeType{
        SuspectedNegativeAndDischarged(162684),
        CuredAndDischarged(159791),
        DischargedAgainstMedicalAdvice(1694),
        SuspectReferredToOtherFacility(142177),
        ConfirmedCaseTransferredToOtherFacility(159392),
        DiedOnWard(160034),
        DeadOnArrival(142934);

        private int outComeId;

        private OutComeType(int outComeId) {
            this.outComeId = outComeId;
        }

        @Override
        public String toString(){
            return String.valueOf(outComeId);
        }
    }

    private List<Concept> getOutComeOptions(ConceptService conceptService) {
        List<Concept> concepts = new ArrayList<Concept>();
        for (OutComeType outComeType : OutComeType.values()) {
            Concept concept = conceptService.getConcept(outComeType.outComeId);
            if (concept != null) {
                concepts.add(concept);
            }
        }
        return concepts;
    }
}
