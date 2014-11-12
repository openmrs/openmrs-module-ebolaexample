package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

public class ProgramFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @FragmentParam("program") Program program,
                           @FragmentParam(value = "enrollmentForm", required = false) Object enrollmentForm,
                           @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                           FragmentModel model) {
        List<PatientProgram> enrollments = programWorkflowService.getPatientPrograms(patient.getPatient(), program, null, null, null, null, false);
        PatientProgram currentEnrollment = null;
        for (PatientProgram candidate : enrollments) {
            if (candidate.getActive()) {
                currentEnrollment = candidate;
                break;
            }
        }

        model.addAttribute("currentEnrollment", currentEnrollment);
        model.addAttribute("enrollmentForm", enrollmentForm);
    }

    public Object enroll(@RequestParam("patient") Patient patient,
                         @RequestParam("program") Program program,
                         UiUtils ui,
                         @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService) {
        PatientProgram enrollment = new PatientProgram();
        enrollment.setPatient(patient);
        enrollment.setProgram(program);
        enrollment.setDateEnrolled(new Date());
        programWorkflowService.savePatientProgram(enrollment);

        return new SuccessResult("Enrolled " + ui.format(patient) + " in " + ui.format(program));
    }

}
