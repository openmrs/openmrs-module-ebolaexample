package org.openmrs.module.ebolaexample.rest.search;


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.WardResource;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Integration tests for the DrugConceptSearchHandler
 */
public class ExpiredDrugOrdersByPatientHandlerTest extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    private WardResource resource;
    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        response = new MockHttpServletResponse();
        requestURI = "order";
    }

    @Test
    public void shouldReturnOnlyDrugConcepts() throws Exception {
        Patient patient = Context.getPatientService().getAllPatients().get(0);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/v1/order");
        request.addParameter("patient", patient.getUuid());
        request.addParameter("expired", "true");
        request.addHeader("content-type", "application/json");
        response = webMethods.handle(request);

        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        List<LinkedHashMap<String, String>> results = (ArrayList<LinkedHashMap<String, String>>) responseObject.get("results");
        assertEquals(getExpiredDrugCount(patient), results.size());
        for (LinkedHashMap<String, String> result : results) {
            Order drugOrder = Context.getOrderService().getOrderByUuid(result.get("uuid"));
            assertTrue(drugOrder.isDiscontinuedRightNow());
        }
    }

    private int getExpiredDrugCount(Patient patient) {
        int expiredCount = 0;
        List<Order> allOrdersByPatient = Context.getOrderService().getAllOrdersByPatient(patient);
        OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
        for (Order order : allOrdersByPatient) {
            if(order.isType(drugOrderType) && order.isDiscontinuedRightNow()) {
                expiredCount += 1;
            }
        }
        return expiredCount;
    }
}