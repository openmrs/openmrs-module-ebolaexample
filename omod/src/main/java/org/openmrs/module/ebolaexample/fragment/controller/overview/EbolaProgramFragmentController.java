package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.List;

public class EbolaProgramFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                           @SpringBean("encounterService") EncounterService encounterService,
                           FragmentModel model) {
        Program program = MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM);
        EncounterType triageEncType = MetadataUtils.existing(EncounterType.class, EbolaMetadata._EncounterType.EBOLA_TRIAGE);

        List<PatientProgram> enrollments = programWorkflowService.getPatientPrograms(patient.getPatient(), program, null, null, null, null, false);
        PatientProgram currentEnrollment = null;
        for (PatientProgram candidate : enrollments) {
            if (candidate.getActive()) {
                currentEnrollment = candidate;
                break;
            }
        }

        model.addAttribute("currentEnrollment", currentEnrollment);
        // TODO this should consider the date bounds of currentEnrollment
        model.addAttribute("triageEncounter", lastEncounter(encounterService, patient.getPatient(), triageEncType));
    }

    private Encounter lastEncounter(EncounterService service, Patient patient, EncounterType type) {
        List<Encounter> encounters = service.getEncounters(patient, null, null, null, null, Arrays.asList(type), null, null, null, false);
        return mostRecent(encounters);
    }

    private Encounter mostRecent(List<Encounter> encounters) {
        Encounter mostRecent = null;
        if (encounters != null) {
            for (Encounter candidate : encounters) {
                if (mostRecent == null || OpenmrsUtil.compare(mostRecent.getEncounterDatetime(), candidate.getEncounterDatetime()) < 0) {
                    mostRecent = candidate;
                }
            }
        }
        return mostRecent;
    }

}
