package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.ebolaexample.EncounterUtil;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Collections;
import java.util.List;

public class EbolaProgramFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                           @SpringBean("encounterService") EncounterService encounterService,
                           @SpringBean("obsService") ObsService obsService,
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

        getObs(patient, obsService, model);

        model.addAttribute("currentEnrollment", currentEnrollment);
        // TODO this should consider the date bounds of currentEnrollment
        model.addAttribute("triageEncounter", EncounterUtil.lastEncounter(encounterService, patient.getPatient(), triageEncType, null));
    }

    private void getObs(PatientDomainWrapper patient, ObsService obsService, FragmentModel model) {
        List<Person> whom = Collections.singletonList((Person) patient.getPatient());
        List<Obs> obs = obsService.getObservationsByPerson(whom.get(0));
        model.addAttribute("mostRecentWeight", null);
        model.addAttribute("ebolaStage", null);
        model.addAttribute("ebolaStageAtAdmission", null);
        model.addAttribute("typeOfPatient", null);

        for(Obs ob : obs){
            String conceptUUID = ob.getConcept().getUuid();
            if(conceptUUID.equals(EbolaMetadata._Concept.WEIGHT_IN_KG)){
                model.addAttribute("mostRecentWeight", ob);
            }else if(conceptUUID.equals(EbolaMetadata._Concept.EBOLA_STAGE)){
                String ebolaStage = "";
                Concept valueCoded = ob.getValueCoded();
                if(valueCoded != null){
                    String uuid = valueCoded.getUuid();
                    if(uuid.equals("162829AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")){
                        ebolaStage = "Stage 1(Early/Dry)";
                    }else if(uuid.equals("162830AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")){
                        ebolaStage = "Stage 2(GI/Wet)";
                    }else if(uuid.equals("162831AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")){
                        ebolaStage = "Stage 3(Severe)";
                    }
                    Form form = ob.getEncounter().getForm();
                    if(form != null && form.getFormId() == 7){
                        if(model.getAttribute("ebolaStage") == null){
                            model.addAttribute("ebolaStage", ebolaStage);
                        }
                    }
                    else if(model.getAttribute("ebolaStageAtAdmission") == null){
                        model.addAttribute("ebolaStageAtAdmission", ebolaStage);
                    }
                }

            }else if(conceptUUID.equals(EbolaMetadata._Concept.TYPE_OF_PATIENT)){
                model.addAttribute("typeOfPatient", ob);
            }
        }
    }

}
