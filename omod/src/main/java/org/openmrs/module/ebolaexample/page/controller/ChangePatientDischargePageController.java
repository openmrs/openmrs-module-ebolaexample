package org.openmrs.module.ebolaexample.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.Visit;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
            if (simpleDateFormat.format(dateCompleted).equalsIgnoreCase(simpleDateFormat.format(new Date()))) {
                dateCompleted = new Date();
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

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            model.put("error", e.getMessage());
            preparePageModel(patientService, programWorkflowService, conceptService, ui, patientUuid, model);
            return null;
        }

        Map<String, Object> args = new HashMap<String, Object>();
        args.put("patient", patient);
        return "redirect:" + ui.pageLink("ebolaexample", "ebolaOverview", args);
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

    private List<Concept> getOutComeOptions(ConceptService conceptService) {
        List<Integer> outComeIds = Arrays.asList(162684, 159791, 1694, 142177, 159392, 160034, 142934);
        List<Concept> concepts = new ArrayList<Concept>();
        for (Integer id : outComeIds) {
            Concept concept = conceptService.getConcept(id);
            if (concept != null) {
                concepts.add(concept);
            }
        }
        return concepts;
    }

}
