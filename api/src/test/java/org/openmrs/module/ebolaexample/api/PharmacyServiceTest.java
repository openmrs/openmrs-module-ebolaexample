package org.openmrs.module.ebolaexample.api;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

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
        dose.setScheduledDatetime(new Date());

        pharmacyService.saveScheduledDose(dose);
    }

}