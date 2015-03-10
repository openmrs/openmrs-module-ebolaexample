package org.openmrs.module.ebolaexample.rest;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DoseHistoryResourceTest extends BaseEbolaResourceTest {

    private String requestURI = "ebola/dosehistory";

    @Test
    public void testGetOne() throws Exception {
        ScheduledDose scheduledDose = createScheduledDose();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI + "/" + scheduledDose.getOrder().getPatient().getUuid());
        request.addHeader("content-type", "application/json");

        SimpleObject response = toSimpleObject(handle(request));
        List dosesByOrder = (List) response.get("dosesByOrder");
        assertThat(dosesByOrder.size(), is(1));
        Map o = (Map) dosesByOrder.get(0);
        Map order = (Map) o.get("order");
        List<Map> doses = (List<Map>) o.get("doses");
        assertThat((String) order.get("uuid"), is(scheduledDose.getOrder().getUuid()));
        assertThat(doses.size(), is(1));
        assertThat((String) doses.get(0).get("uuid"), is(scheduledDose.getUuid()));
    }

    private ScheduledDose createScheduledDose() {
        PharmacyService pharmacyService = Context.getService(PharmacyService.class);
        return pharmacyService.saveScheduledDose(buildScheduledDose());
    }

    private ScheduledDose buildScheduledDose() {
        DrugOrder order = (DrugOrder) Context.getOrderService().getOrder(1);
        ScheduledDose dose = new ScheduledDose();
        dose.setOrder(order);
        dose.setDateCreated(new Date());
        dose.setStatus(ScheduledDose.DoseStatus.PARTIAL);
        dose.setReasonNotAdministeredNonCoded("Illnesses");
        dose.setScheduledDatetime(order.getDateActivated());
        return dose;
    }

}