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
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/encounter/laboratory")
public class LaboratoryEncounterController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public SimpleObject getAll(@RequestParam String patientUuid, @RequestParam(required = false, defaultValue = "0") int top){
        SimpleObject response = new SimpleObject();
        EncounterService encounterService = Context.getEncounterService();
        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);

        Form form = Context.getFormService().getFormByUuid(EbolaMetadata._Form.EBOLA_LAB_FORM);

        List<Encounter> allEncounters = encounterService.getEncounters(patient, null, null, null, Arrays.asList(form), Arrays.asList(encounterType), null, null, null, false);
        Collections.reverse(allEncounters);
        HashMap<String, ArrayList<SimpleObject>> obsMap = new HashMap<String, ArrayList<SimpleObject>>();
        for(Encounter encounter: allEncounters){
            Set<Obs> allObs = encounter.getAllObs();
            for(Obs obs : allObs){
                String uuid = obs.getConcept().getUuid();
                SimpleObject obsDTO = new SimpleObject().add("encounterDatetime", encounter.getEncounterDatetime()).add("value", obs.getValueCoded().getUuid());

                if(obsMap.containsKey(uuid)){
                    ArrayList<SimpleObject> obsArray = obsMap.get(uuid);
                    if(top == 0 || obsArray.size()< top) {
                        obsArray.add(obsDTO);
                    }
                }
                else{
                    ArrayList<SimpleObject> obsArray = new ArrayList<SimpleObject>();
                    obsArray.add(obsDTO);
                    obsMap.put(uuid, obsArray);
                }
            }
        }
        ArrayList<Map.Entry<String, ArrayList<SimpleObject>>> entries = new ArrayList<Map.Entry<String, ArrayList<SimpleObject>>>(obsMap.entrySet());
        response.add("encounters", entries);
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
