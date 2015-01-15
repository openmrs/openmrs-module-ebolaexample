package org.openmrs.module.ebolaexample.api.impl;

import org.openmrs.Order;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.db.ScheduledDoseDAO;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;

import java.util.List;

public class PharmacyServiceImpl extends BaseOpenmrsService implements PharmacyService {

    private ScheduledDoseDAO scheduledDoseDAO;

    public void setScheduledDoseDAO(ScheduledDoseDAO scheduledDoseDAO) {
        this.scheduledDoseDAO = scheduledDoseDAO;
    }

    @Override
    public ScheduledDose saveScheduledDose(ScheduledDose dose) {
        return scheduledDoseDAO.saveOrUpdate(dose);
    }

    @Override
    public List<ScheduledDose> getAllScheduledDoses() {
        return scheduledDoseDAO.getAll();
    }

    @Override
    public ScheduledDose getScheduledDoseByUuid(String uuid) {
        return scheduledDoseDAO.getScheduledDoseByUuid(uuid);
    }

    @Override
    public List<ScheduledDose> getScheduledDosesForOrder(Order order) {
        return scheduledDoseDAO.getScheduledDoseByOrderId(order);
    }
}
