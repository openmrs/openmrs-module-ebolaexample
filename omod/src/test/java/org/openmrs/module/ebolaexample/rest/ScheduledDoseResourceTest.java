package org.openmrs.module.ebolaexample.rest;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ScheduledDoseResourceTest extends BaseEbolaResourceTest {

    private String requestURI = "ebola/scheduled-dose";

    @Test
    public void testGetOne() throws Exception {
        ScheduledDose scheduledDose = createScheduledDose();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI + '/' + scheduledDose.getUuid());
        request.addHeader("content-type", "application/json");

        SimpleObject response = toSimpleObject(handle(request));

        assertEquals(scheduledDose.getUuid(), response.get("uuid"));
        assertEquals(scheduledDose.getStatus().name(), response.get("status"));
        assertEquals(scheduledDose.getReasonNotAdministeredNonCoded(), response.get("reasonNotAdministeredNonCoded"));
        assertEquals("PARTIAL - " + scheduledDose.getDateCreated().toString(), response.get("display"));
    }

    @Test
    public void testSaveSingleScheduledDose() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrder(1);
        request.setContent(("{\"status\": \"FULL\", " +
                "\"reasonNotAdministeredNonCoded\": \"Patient Illnesses\"," +
                "\"order\": \"" + drugOrder.getUuid() + "\"}").getBytes());
        MockHttpServletResponse handled = handle(request);
        SimpleObject response = toSimpleObject(handled);
        assertEquals("FULL", response.get("status"));
        assertEquals("Patient Illnesses", response.get("reasonNotAdministeredNonCoded"));
        LinkedHashMap<String, String> order = (LinkedHashMap<String, String>) response.get("order");
        assertEquals(drugOrder.getUuid(), order.get("uuid"));
    }

    @Test
    public void testSavingSetsRequiredFields() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        request.setContent(("{\"status\": \"FULL\", " +
                "\"reasonNotAdministeredNonCoded\": \"Patient Illnesses\"}").getBytes());
        MockHttpServletResponse handled = handle(request);
        SimpleObject response = toSimpleObject(handled);
        assertNotNull(response.get("dateCreated"));

        PharmacyService pharmacyService = Context.getService(PharmacyService.class);
        ScheduledDose scheduledDose = pharmacyService.getScheduledDoseByUuid((String) response.get("uuid"));

        assertNotNull(scheduledDose.getScheduledDoseId());
        assertNotNull(scheduledDose.getScheduledDatetime());
        assertNotNull(scheduledDose.getCreator());
    }

    @Test
    public void testGetForPatient() throws Exception {
        ScheduledDose scheduledDose = createScheduledDose();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addParameter("patient", scheduledDose.getOrder().getPatient().getUuid());
        request.addHeader("content-type", "application/json");

        SimpleObject response = toSimpleObject(handle(request));
        List<Map> results = (List<Map>) response.get("results");
        assertEquals(1, results.size());
        assertEquals(scheduledDose.getUuid(), results.get(0).get("uuid"));
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
        dose.setScheduledDatetime(new Date());
        return dose;
    }
}