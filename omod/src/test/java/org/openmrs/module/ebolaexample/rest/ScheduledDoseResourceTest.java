package org.openmrs.module.ebolaexample.rest;

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.FreeTextDosingInstructions;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.text.SimpleDateFormat;
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
        request.setContent(("{\"status\": \"PARTIAL\", " +
                "\"scheduledDatetime\": \"" + jsDate(drugOrder.getDateActivated()) + "\", " +
                "\"reasonNotAdministeredNonCoded\": \"Patient Illnesses\"," +
                "\"order\": \"" + drugOrder.getUuid() + "\"}").getBytes());
        MockHttpServletResponse handled = handle(request);
        SimpleObject response = toSimpleObject(handled);
        assertEquals("PARTIAL", response.get("status"));
        assertEquals("Patient Illnesses", response.get("reasonNotAdministeredNonCoded"));
        LinkedHashMap<String, String> order = (LinkedHashMap<String, String>) response.get("order");
        assertEquals(drugOrder.getUuid(), order.get("uuid"));
    }

    @Test
    public void testSavingSetsRequiredFields() throws Exception {
        Patient patient = Context.getPatientService().getPatient(7);

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setLocation(Context.getLocationService().getLocation(1));
        encounter.setEncounterDatetime(new Date());
        Context.getEncounterService().saveEncounter(encounter);

        DrugOrder prescription = new DrugOrder();
        prescription.setEncounter(encounter);
        prescription.setOrderer(Context.getProviderService().getProvider(1));
        prescription.setDrug(Context.getConceptService().getDrug(3));
        prescription.setConcept(prescription.getDrug().getConcept());
        prescription.setDosingType(FreeTextDosingInstructions.class);
        prescription.setDosingInstructions("Take the med");
        prescription.setDateActivated(new Date());
        prescription.setPatient(patient);
        prescription.setCareSetting(Context.getOrderService().getCareSetting(2));
        Order order = Context.getOrderService().saveOrder(prescription, null);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/rest/" + RestConstants.VERSION_1 + "/"
                + requestURI);
        request.addHeader("content-type", "application/json");
        request.setContent(("{\"order\": \"" + order.getUuid() + "\", " +
                "\"status\": \"PARTIAL\", " +
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
        dose.setScheduledDatetime(order.getDateActivated());
        return dose;
    }

    private String jsDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
    }
}