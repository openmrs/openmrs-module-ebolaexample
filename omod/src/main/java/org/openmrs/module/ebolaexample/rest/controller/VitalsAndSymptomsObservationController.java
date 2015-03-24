package org.openmrs.module.ebolaexample.rest.controller;


import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/encounter/vitals-and-symptoms")
public class VitalsAndSymptomsObservationController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public SimpleObject getAll(@RequestParam String patientUuid, @RequestParam String formUuid, @RequestParam(required = false, defaultValue = "0") int top){
        SimpleObject response = new SimpleObject();
        EncounterService encounterService = Context.getEncounterService();
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);

        if(!formUuid.equals(EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS) &&
                !formUuid.equals(EbolaMetadata._Form.EBOLA_VITALS_FORM)){
            throw new UnsupportedOperationException("The form uuid can be only vital or symptoms form uuid");
        }
        Form form = Context.getFormService().getFormByUuid(formUuid);

        ArrayList<SimpleObject> encounters = new ArrayList<SimpleObject>();
        List<Encounter> allEncounters = encounterService.getEncounters(patient, null, null, null, Arrays.asList(form), Arrays.asList(encounterType), null, null, null, false);
        Collections.reverse(allEncounters);
        if(top!=0){
            allEncounters = allEncounters.subList(0, top);
        }
        for(Encounter encounter: allEncounters){
            ArrayList<SimpleObject> obsResult = getAllObs(encounter);
            SimpleObject encounterDTO = new SimpleObject()
                    .add("uuid", encounter.getUuid())
                    .add("dateCreated", encounter.getEncounterDatetime())
                    .add("obs", obsResult);
            encounters.add(encounterDTO);
        }

        response.add("encounters", encounters);
        return response;
    }

    private ArrayList<SimpleObject> getAllObs(Encounter encounter) {
        ArrayList<SimpleObject> obsResult = new ArrayList<SimpleObject>();
        if(encounter  != null ){
            Set<Obs> allObs = encounter.getAllObs();

            for(Obs obs : allObs){
                if(obs.getObsGroup() != null){
                    continue;
                }
                if(obs.isObsGrouping()){
                    SimpleObject result = new SimpleObject();
                    result.add("concept", obs.getConcept().getUuid());
                    ArrayList<SimpleObject> group = new ArrayList<SimpleObject>();
                    for(Obs child : obs.getGroupMembers()){
                        group.add(getObsConceptPair(child));
                    }
                    result.add("groupMembers", group);
                    obsResult.add(result);
                }else{
                    SimpleObject pair = getObsConceptPair(obs);
                    obsResult.add(pair);
                }
            }
        }
        return obsResult;
    }

    private SimpleObject getObsConceptPair(Obs obs) {
        String conceptUuid = obs.getConcept().getUuid();
        Object conceptValue = getValue(obs);
        return new SimpleObject().add("concept", conceptUuid).add("value", conceptValue);
    }

    private Object getValue(Obs obs) {
        Concept concept = obs.getConcept();
        ConceptDatatype datatype = concept.getDatatype();
        if(datatype.isBoolean()){
            return obs.getValueBoolean();
        }
        else if(datatype.isCoded()){
            return obs.getValueCoded().getUuid();
        }
        else if(datatype.isDate()){
            return obs.getValueDate();
        }
        else if(datatype.isDateTime()){
            return obs.getValueDatetime();
        }
        else if(datatype.isNumeric()){
            return obs.getValueNumeric();
        }
        else if(datatype.isComplex()){
            return obs.getValueComplex();
        }

        return null;
    }
}
