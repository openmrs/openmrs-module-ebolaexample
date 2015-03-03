package org.openmrs.module.ebolaexample.api;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.ebolaexample.pharmacy.DoseHistory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface PharmacyService extends OpenmrsService {

    @Transactional
    ScheduledDose saveScheduledDose(ScheduledDose dose);

    /** Also returns voided values */
    @Transactional(readOnly = true)
    List<ScheduledDose> getAllScheduledDoses();

    @Transactional(readOnly = true)
    ScheduledDose getScheduledDoseByUuid(String uuid);

    @Transactional(readOnly = true)
    List<ScheduledDose> getScheduledDosesForOrder(Order order);

    @Transactional(readOnly = true)
    DoseHistory getScheduledDosesByPatientAndDateRange(Patient patient, Date onOrAfter, Date onOrBefore, boolean includeVoided);

    @Transactional(readOnly = true)
    DoseHistory getScheduledDosesByPatient(Patient patient);

}
