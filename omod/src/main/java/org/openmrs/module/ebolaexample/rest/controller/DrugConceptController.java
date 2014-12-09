package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.dwr.ConceptListItem;
import org.openmrs.web.dwr.DWRConceptService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/drug-concept")
public class DrugConceptController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object get(@RequestParam(value="query", required=false) String phrase) {
        SimpleObject response = new SimpleObject();
        ArrayList<String> emptyList = new ArrayList<String>();
        ArrayList<String> includeClassNames = new ArrayList<String>();
        includeClassNames.add("Drug");
        List<Object> concepts = new DWRConceptService()
                .findConcepts(phrase, false, includeClassNames, emptyList, emptyList, emptyList, false);
        response.add("drugConcepts", mappedConcepts(concepts));
        return response;
    }

    private List<SimpleObject> mappedConcepts(List<Object> unmappedConcepts) {
        List<SimpleObject> mappedConcepts = new ArrayList<SimpleObject>();
        for (Object concept : unmappedConcepts) {
            SimpleObject mappedConcept = new SimpleObject();
            mappedConcept.add("display", ((ConceptListItem) concept).getName());
            mappedConcept.add("conceptId", ((ConceptListItem) concept).getConceptId());
            mappedConcepts.add(mappedConcept);
        }
        Collections.sort(mappedConcepts, new Comparator<SimpleObject>() {
            @Override
            public int compare(SimpleObject o1, SimpleObject o2) {
                return ((String) o1.get("display")).compareToIgnoreCase((String) o2.get("display"));
            }
        });
        return mappedConcepts;
    }
}

