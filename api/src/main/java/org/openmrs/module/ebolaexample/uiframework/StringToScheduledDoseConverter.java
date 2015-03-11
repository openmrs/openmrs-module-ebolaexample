package org.openmrs.module.ebolaexample.uiframework;

import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToScheduledDoseConverter implements Converter<String, ScheduledDose> {

    @Autowired
    private PharmacyService pharmacyService;

    @Override
    public ScheduledDose convert(String uuid) {
        return pharmacyService.getScheduledDoseByUuid(uuid);
    }
}
