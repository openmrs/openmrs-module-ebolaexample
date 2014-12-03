package org.openmrs.module.ebolaexample.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;

public interface PharmacyService extends OpenmrsService {

//    List<ScheduledDose> getScheduledDosesFor(DrugOrder order);

    ScheduledDose saveScheduledDose(ScheduledDose dose);

}
