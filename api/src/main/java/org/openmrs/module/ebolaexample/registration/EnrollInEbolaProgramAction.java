package org.openmrs.module.ebolaexample.registration;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.registrationapp.action.AfterPatientCreatedAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component("enrollInEbolaProgramAfterPatientCreatedAction")
public class EnrollInEbolaProgramAction implements AfterPatientCreatedAction {

    @Autowired
    ProgramWorkflowService programWorkflowService;

    public void setProgramWorkflowService(ProgramWorkflowService programWorkflowService) {
        this.programWorkflowService = programWorkflowService;
    }

    @Override
    public void afterPatientCreated(Patient patient, Map<String, String[]> params) {
        Program ebolaProgram = MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM);

        PatientProgram enrollment = new PatientProgram();
        enrollment.setProgram(ebolaProgram);
        enrollment.setPatient(patient);
        enrollment.setDateEnrolled(new Date());

        programWorkflowService.savePatientProgram(enrollment);
    }

}
