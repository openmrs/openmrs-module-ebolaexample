package org.openmrs.module.ebolaexample.api;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.ebolaexample.pharmacy.DoseHistory;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PharmacyServiceTest extends BaseModuleContextSensitiveTest {

    @Autowired
    PharmacyService pharmacyService;

    @Autowired
    OrderService orderService;

    @Test
    public void testCreatingAScheduledDose() throws Exception {
        DrugOrder order = (DrugOrder) orderService.getOrder(1);

        ScheduledDose dose = new ScheduledDose();
        dose.setOrder(order);
        dose.setScheduledDatetime(new SimpleDateFormat("yyyy-MM-dd").parse("2008-08-09"));
        dose.setStatus(ScheduledDose.DoseStatus.FULL);

        pharmacyService.saveScheduledDose(dose);
    }

    @Test
    public void testGetScheduledDosesByPatientAndDateRange() throws Exception {
        DrugOrder order = (DrugOrder) orderService.getOrder(1);
        Patient patient = order.getPatient();
        DrugOrder orderForOtherPatient = (DrugOrder) orderService.getOrder(2);

        ScheduledDose dose1 = createDose(order, "2008-08-08 09:00");
        ScheduledDose dose2 = createDose(order, "2008-08-09 17:00");
        ScheduledDose dose3 = createDose(order, "2008-08-10 09:15");
        createDose(order, "2008-08-11 09:15");
        createDose(orderForOtherPatient, "2007-12-03 09:00");

        DoseHistory doseHistory = pharmacyService.getScheduledDosesByPatientAndDateRange(patient, date("2008-08-08 00:00"), date("2008-08-10 23:59"), false);
        assertThat(doseHistory.getDoses(), contains(is(dose1), is(dose2), is(dose3)));
    }

    @Test
    public void testGetAllScheduledDoses() throws Exception {
        DrugOrder order = (DrugOrder) orderService.getOrder(1);
        DrugOrder orderForOtherPatient = (DrugOrder) orderService.getOrder(2);

        ScheduledDose dose1 = createDose(order, "2008-08-08 09:00");
        ScheduledDose dose2 = createDose(order, "2008-08-09 17:00");
        ScheduledDose dose3 = createDose(order, "2008-08-10 09:15");
        ScheduledDose dose4 = createDose(order, "2008-08-11 09:15");
        ScheduledDose dose5 = createDose(orderForOtherPatient, "2007-12-03 09:00");

        List<ScheduledDose> allScheduledDoses = pharmacyService.getAllScheduledDoses();
        assertThat(allScheduledDoses, contains(dose1, dose2, dose3, dose4, dose5));
    }

    private ScheduledDose createDose(DrugOrder order, String ymdhm) throws ParseException {
        ScheduledDose scheduledDose = new ScheduledDose();
        scheduledDose.setOrder(order);
        scheduledDose.setScheduledDatetime(date(ymdhm));
        scheduledDose.setStatus(ScheduledDose.DoseStatus.FULL);
        return pharmacyService.saveScheduledDose(scheduledDose);
    }

    private Date date(String ymdhm) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(ymdhm);
    }
}