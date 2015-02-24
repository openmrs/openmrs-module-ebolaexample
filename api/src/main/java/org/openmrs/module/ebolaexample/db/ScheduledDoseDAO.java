package org.openmrs.module.ebolaexample.db;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.ui.framework.db.SingleClassDAO;

import java.util.Date;
import java.util.List;

public interface ScheduledDoseDAO extends SingleClassDAO<ScheduledDose> {

    ScheduledDose getScheduledDoseByUuid(String uuid);

    List<ScheduledDose> getScheduledDoseByOrderId(Order order);

    List<ScheduledDose> getScheduledDosesByPatientAndDateRange(Date onOrAfter, Date onOrBefore, Patient patient, boolean includeVoided);
}
