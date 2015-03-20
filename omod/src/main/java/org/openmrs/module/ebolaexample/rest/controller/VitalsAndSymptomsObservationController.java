package org.openmrs.module.ebolaexample.rest.controller;


import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.EncounterUtil;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Set;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/vitals-and-symptoms-obs")
public class VitalsAndSymptomsObservationController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public SimpleObject getLatest(@RequestParam String patientUuid, @RequestParam String formUuid){
        SimpleObject response = new SimpleObject();
        EncounterService encounterService = Context.getEncounterService();
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);

        if(!formUuid.equals(EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS) &&
                !formUuid.equals(EbolaMetadata._Form.EBOLA_VITALS_FORM)){
            throw new UnsupportedOperationException("The form uuid can be only vital or symptoms form uuid");
        }
        Form form = Context.getFormService().getFormByUuid(formUuid);

        Encounter encounter = EncounterUtil.lastEncounter(encounterService, patient, encounterType, Arrays.asList(form));
        Set<Obs> allObs = encounter.getAllObs();
        response.add("obs", ConversionUtil.convertToRepresentation(allObs, Representation.DEFAULT));
        return response;
    }
}
