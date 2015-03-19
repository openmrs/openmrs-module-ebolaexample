package org.openmrs.module.ebolaexample.rest.controller;


import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
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

import java.util.Set;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/vitals-and-symptoms-obs")
public class VitalsAndSymptomsObservationController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public SimpleObject getLatest(@RequestParam String patientUuid){
        SimpleObject response = new SimpleObject();
        EncounterService encounterService = Context.getEncounterService();
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);
        Encounter encounter = EncounterUtil.lastEncounter(encounterService, patient, encounterType);
        Set<Obs> allObs = encounter.getAllObs();
        response.add("obs", ConversionUtil.convertToRepresentation(allObs, Representation.DEFAULT));
        return response;
    }
}
