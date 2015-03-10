package org.openmrs.module.ebolaexample.rest.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.DrugOrder;
import org.openmrs.FreeTextDosingInstructions;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DrugDoseHistoryControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    @Autowired
    private PharmacyService pharmacyService;

    @Autowired
    private OrderService orderService;

    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        response = new MockHttpServletResponse();
        requestURI =  "/rest/" + RestConstants.VERSION_1 + "/ebola/drug-dose-history/";
        setUpDurationUnits();
    }

    private void setUpDurationUnits() {
        ConceptService conceptService = Context.getConceptService();
        ConceptMapType sameAs = conceptService.getConceptMapTypeByName("same-as");
        ConceptSource snomed = conceptService.getConceptSourceByName("SNOMED CT");
        ConceptReferenceTerm term = new ConceptReferenceTerm(snomed, "258703001", null);
        conceptService.saveConceptReferenceTerm(term);
        Concept days = conceptService.getConcept(28);
        days.addConceptMapping(new ConceptMap(term, sameAs));
        conceptService.saveConcept(days);
    }

    @Test
    public void shouldDrugAndAssociatedDoses() throws Exception {
        Patient patient = Context.getPatientService().getAllPatients().get(2);
        List<Order> allOrdersByPatient = Context.getOrderService().getAllOrdersByPatient(patient);

        DrugOrder drugOrder = createValidDrugOrder(patient);
        ScheduledDose scheduledDose = createValidScheduledDose(drugOrder);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI + patient.getUuid());
        request.addHeader("content-type", "application/json");

        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);

        List<Map> results = (List<Map>) responseObject.get("results");
        assertEquals(2, results.size());
        Map doseHistory = getOrderFromResultByUuid(results, drugOrder.getUuid());
        assertEquals(drugOrder.getUuid(), doseHistory.get("uuid"));
        List doses = (List) doseHistory.get("doses");
        assertEquals(1, doses.size());
        assertEquals(scheduledDose.getUuid(), ((LinkedHashMap) doses.get(0)).get("uuid"));
    }

    private Map getOrderFromResultByUuid(List<Map> results, String uuid) {
        for (Map result : results) {
            if(result.get("uuid").equals(uuid)) {
                return result;
            }
        }
        return null;
    }

    private ScheduledDose createValidScheduledDose(DrugOrder drugOrder) {
        ScheduledDose scheduledDose = new ScheduledDose();
        scheduledDose.setReasonNotAdministeredNonCoded("Anxiety");
        scheduledDose.setOrder(drugOrder);
        scheduledDose.setStatus(ScheduledDose.DoseStatus.PARTIAL);
        scheduledDose.setScheduledDatetime(drugOrder.getDateActivated());
        pharmacyService.saveScheduledDose(scheduledDose);
        return scheduledDose;
    }

    private DrugOrder createValidDrugOrder(Patient patient) {
        DrugOrder drugOrder = new DrugOrder();
        drugOrder.setDosingInstructions("Morning, Evening");
        drugOrder.setDosingType(FreeTextDosingInstructions.class);
        drugOrder.setDuration(5);
        drugOrder.setConcept(Context.getConceptService().getConceptByUuid("15f83cd6-64e9-4e06-a5f9-364d3b14a43d"));
        drugOrder.setDurationUnits(Context.getConceptService().getConceptByUuid("7bfdcbf0-d9e7-11e3-9c1a-0800200c9a66"));
        drugOrder.setFrequency(orderService.getOrderFrequency(3000));
        drugOrder.setCareSetting(Context.getOrderService().getCareSetting(2));
        drugOrder.setPatient(patient);
        drugOrder.setEncounter(Context.getEncounterService().getEncountersByPatient(patient).get(0));
        drugOrder.setOrderer(Context.getProviderService().getAllProviders().get(0));
        drugOrder.setQuantityUnits(Context.getConceptService().getConcept(51));
        drugOrder.setQuantity(1.0);
        orderService.saveOrder(drugOrder, null);
        return drugOrder;
    }
}