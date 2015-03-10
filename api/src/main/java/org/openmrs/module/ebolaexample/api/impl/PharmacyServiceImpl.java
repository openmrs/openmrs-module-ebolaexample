package org.openmrs.module.ebolaexample.api.impl;

import org.joda.time.DateTime;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.db.ScheduledDoseDAO;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.ebolaexample.pharmacy.DoseHistory;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PharmacyServiceImpl extends BaseOpenmrsService implements PharmacyService {

    private ScheduledDoseDAO scheduledDoseDAO;

    private OrderService orderService;

    public void setScheduledDoseDAO(ScheduledDoseDAO scheduledDoseDAO) {
        this.scheduledDoseDAO = scheduledDoseDAO;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
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

    @Override
    public DoseHistory getScheduledDosesByPatientAndDateRange(Patient patient, Date onOrAfter, Date onOrBefore, boolean includeVoided) {
        List<DrugOrder> orders = new ArrayList<DrugOrder>();
        for (Order candidate : orderService.getAllOrdersByPatient(patient)) {
            if (candidate instanceof DrugOrder
                    && OpenmrsUtil.compareWithNullAsEarliest(candidate.getEffectiveStartDate(), onOrBefore) <= 0
                    && OpenmrsUtil.compareWithNullAsLatest(candidate.getEffectiveStopDate(), onOrAfter) >= 0) {
                orders.add((DrugOrder) candidate);
            }
        }

        if (onOrBefore == null) {
            onOrBefore = new Date();
        }
        if (onOrAfter == null) {
            onOrAfter = new DateTime(onOrBefore).minusYears(10).toDate();
        }
        List<ScheduledDose> doses = scheduledDoseDAO.getScheduledDosesByPatientAndDateRange(onOrAfter, DateUtil.getEndOfDayIfTimeExcluded(onOrBefore), patient,
                includeVoided);

        return new DoseHistory(orders, doses);
    }

    @Override
    public DoseHistory getScheduledDosesByPatient(Patient patient) {
        List<DrugOrder> orders = new ArrayList<DrugOrder>();
        for (Order candidate : orderService.getAllOrdersByPatient(patient)) {
            if (candidate instanceof DrugOrder) {
                orders.add((DrugOrder) candidate);
            }
        }

        List<ScheduledDose> doses = scheduledDoseDAO.getScheduledDosesByPatient(patient, true);

        return new DoseHistory(orders, doses);
    }
}
