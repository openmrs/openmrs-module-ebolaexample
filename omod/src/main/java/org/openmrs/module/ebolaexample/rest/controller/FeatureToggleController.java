package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.module.ebolaexample.api.FeatureToggleService;
import org.openmrs.module.ebolaexample.domain.FeatureToggle;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/feature-toggle")
public class FeatureToggleController {

    @Autowired
    FeatureToggleService featureToggleService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object getAll(){
        List<FeatureToggle> featureToggles = featureToggleService.getAll();
        SimpleObject response = new SimpleObject();
        List<SimpleObject> toggles = new ArrayList<SimpleObject>();
        for(FeatureToggle toggle : featureToggles){
            toggles.add(new SimpleObject().add("name", toggle.getName()).add("enabled", toggle.getEnabled()));
        }
        response.add("featureToggles", toggles);
        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestBody Map<String, Object> map){
        String featureName = (String) map.get("featureName");
        String action = (String) map.get("action");
        FeatureToggle featureToggle = new FeatureToggle();

        if(action.equals("turnOn")){
            featureToggle = featureToggleService.turnOn(featureName);
        }else if(action.equals("turnOff")){
            featureToggle = featureToggleService.turnOff(featureName);
        }else{
            throw new UnsupportedOperationException(action + " is not supported.");
        }

        SimpleObject response = new SimpleObject();
        response.add("featureToggle", new SimpleObject().add("name", featureToggle.getName()).add("enabled", featureToggle.getEnabled()));
        return response;
    }
}
