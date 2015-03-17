package org.openmrs.module.ebolaexample.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ebolaexample.api.FeatureToggleService;
import org.openmrs.module.ebolaexample.db.FeatureToggleDAO;
import org.openmrs.module.ebolaexample.domain.FeatureToggle;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FeatureToggleServiceImpl extends BaseOpenmrsService implements FeatureToggleService {
    @Autowired
    FeatureToggleDAO featureToggleDAO;

    public void setFeatureToggleDAO(FeatureToggleDAO featureToggleDAO){this.featureToggleDAO = featureToggleDAO;}
    @Override
    public List<FeatureToggle> getAll(){
        List<FeatureToggle> featureToggles = featureToggleDAO.getAll();
        return featureToggles;
    }

    @Override
    public FeatureToggle saveOrUpdate(FeatureToggle toggle) {
        featureToggleDAO.saveOrUpdate(toggle);
        return toggle;
    }

    @Override
    public FeatureToggle turnOn(String toggleName){
        FeatureToggle toggle = featureToggleDAO.getByName(toggleName);
        if(toggle == null){
            toggle = new FeatureToggle();
            toggle.setName(toggleName);
        }
        toggle.setEnabled(true);
        featureToggleDAO.saveOrUpdate(toggle);
        return toggle;
    }

    @Override
    public FeatureToggle turnOff(String toggleName){
        FeatureToggle toggle = featureToggleDAO.getByName(toggleName);
        if(toggle == null){
            toggle = new FeatureToggle();
            toggle.setName(toggleName);
        }
        toggle.setEnabled(false);
        featureToggleDAO.saveOrUpdate(toggle);
        return toggle;
    }
}
