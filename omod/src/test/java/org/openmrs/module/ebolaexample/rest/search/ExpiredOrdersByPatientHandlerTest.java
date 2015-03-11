package org.openmrs.module.ebolaexample.rest.search;


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
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
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.openmrs.module.ebolaexample.metadata.EbolaMetadata._OrderType.IV_FLUID_ORDER_TYPE_UUID;

/**
 * Integration tests for the DrugConceptSearchHandler
 */
public class ExpiredOrdersByPatientHandlerTest extends BaseModuleWebContextSensitiveTest {
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
        assertEquals(getExpiredOrderCount(patient, OrderType.DRUG_ORDER_TYPE_UUID), results.size());
        for (LinkedHashMap<String, String> result : results) {
            Order drugOrder = Context.getOrderService().getOrderByUuid(result.get("uuid"));
            assertFalse(drugOrder.isActive());
        }
    }

    @Test
    public void shouldReturnOnlyIvFluids() throws Exception {
        Patient patient = Context.getPatientService().getAllPatients().get(2);
        ebolaTestData.createOrder(patient, IV_FLUID_ORDER_TYPE_UUID, "15f83cd6-64e9-4e06-a5f9-364d3b14a43d", false);
        ebolaTestData.createOrder(patient, IV_FLUID_ORDER_TYPE_UUID, "d144d24f-6913-4b63-9660-a9108c2bebef", false);


        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/v1/order");
        request.addParameter("t", "ivfluidorder");
        request.addParameter("patient", patient.getUuid());
        request.addParameter("expired", "true");
        request.addHeader("content-type", "application/json");
        response = webMethods.handle(request);

        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        List<LinkedHashMap<String, String>> results = (ArrayList<LinkedHashMap<String, String>>) responseObject.get("results");
        assertEquals(getExpiredOrderCount(patient, EbolaMetadata._OrderType.IV_FLUID_ORDER_TYPE_UUID), results.size());
        for (LinkedHashMap<String, String> result : results) {
            IvFluidOrder ivFluidOrder = (IvFluidOrder) Context.getOrderService().getOrderByUuid(result.get("uuid"));
            assertFalse(ivFluidOrder.isActive());
        }
    }

    private int getExpiredOrderCount(Patient patient, String orderTypeUuid) {
        int expiredCount = 0;
        List<Order> allOrdersByPatient = Context.getOrderService().getAllOrdersByPatient(patient);
        OrderType requestedOrderType = Context.getOrderService().getOrderTypeByUuid(orderTypeUuid);
        for (Order order : allOrdersByPatient) {
            if(order.isType(requestedOrderType) && !order.isActive()) {
                expiredCount += 1;
            }
        }
        return expiredCount;
    }
}