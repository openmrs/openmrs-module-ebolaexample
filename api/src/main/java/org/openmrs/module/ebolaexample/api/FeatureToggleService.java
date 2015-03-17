package org.openmrs.module.ebolaexample.api;

import org.openmrs.module.ebolaexample.domain.FeatureToggle;

import java.util.List;

public interface FeatureToggleService {
    List<FeatureToggle> getAll();
    FeatureToggle saveOrUpdate(FeatureToggle toggle);
    FeatureToggle turnOn(String toggleName);

    FeatureToggle turnOff(String toggleName);
}
