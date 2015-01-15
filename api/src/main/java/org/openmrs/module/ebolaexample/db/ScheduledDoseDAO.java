package org.openmrs.module.ebolaexample.db;

import org.openmrs.Order;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.ui.framework.db.SingleClassDAO;

import java.util.List;

public interface ScheduledDoseDAO extends SingleClassDAO<ScheduledDose> {

    public ScheduledDose getScheduledDoseByUuid(String uuid);

    public List<ScheduledDose> getScheduledDoseByOrderId(Order order);

}
