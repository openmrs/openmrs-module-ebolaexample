package org.openmrs.module.ebolaexample.rest;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.db.HibernateScheduledDoseDAO;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

public class ScheduledDoseResourceTest extends BaseEbolaResourceTest {

    @Autowired
    PharmacyService pharmacyService;

    private String requestURI = "ebola/scheduled-dose";

    @Test
    public void testGetOne() throws Exception {
        ScheduledDose scheduledDose = createScheduledDose();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI + '/' + scheduledDose.getUuid());
        request.addHeader("content-type", "application/json");

        SimpleObject response = toSimpleObject(handle(request));

        assertEquals(scheduledDose.getUuid(), response.get("uuid"));
        assertEquals(scheduledDose.getStatus(), response.get("status"));
        assertEquals(scheduledDose.getReasonNotAdministeredNonCoded(), response.get("reasonNotAdministeredNonCoded"));
        assertEquals("PARTIAL - " + scheduledDose.getDateCreated().toString(), response.get("display"));
    }

    private ScheduledDose createScheduledDose() {
        DrugOrder order = (DrugOrder) Context.getOrderService().getOrder(1);

        ScheduledDose dose = new ScheduledDose();
        dose.setOrder(order);
        dose.setDateCreated(new Date());
        dose.setStatus("PARTIAL");
        dose.setReasonNotAdministeredNonCoded("Illnesses");
        dose.setScheduledDatetime(new Date());

        pharmacyService.saveScheduledDose(dose);
        return dose;
    }
}