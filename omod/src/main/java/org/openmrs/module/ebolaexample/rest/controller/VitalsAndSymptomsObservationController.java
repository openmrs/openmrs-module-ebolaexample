package org.openmrs.module.ebolaexample.rest.controller;


import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.EncounterUtil;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
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
        ArrayList<SimpleObject> obsResult = new ArrayList<SimpleObject>();

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
        response.add("obs", obsResult);
        return response;
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
