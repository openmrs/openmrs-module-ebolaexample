package org.openmrs.module.ebolaexample.api;

import org.openmrs.Order;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PharmacyService extends OpenmrsService {

    ScheduledDose saveScheduledDose(ScheduledDose dose);

    List<ScheduledDose> getAllScheduledDoses();

    ScheduledDose getScheduledDoseByUuid(String uuid);

    List<ScheduledDose> getScheduledDosesForOrder(Order order);

}
