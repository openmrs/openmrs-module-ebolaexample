package org.openmrs.module.ebolaexample.rest.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.EbolaWebRestTestBase;
import org.openmrs.module.ebolaexample.api.IvFluidOrderStatusService;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.domain.IvFluidOrderStatus;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.LinkedHashMap;

import static org.openmrs.module.ebolaexample.metadata.EbolaMetadata._OrderType.IV_FLUID_ORDER_TYPE_UUID;

public class IvFluidOrderStatusControllerTest  extends EbolaWebRestTestBase {

    private String requestURI = "/rest/" + RestConstants.VERSION_1 + "/ebola/ivfluid-order-status";

    @Autowired
    OrderService orderService;

    @Autowired
    IvFluidOrderStatusService ivFluidOrderStatusService;

    private IvFluidOrder ivFluidOrder;

    @Before
    public void setUp() throws Exception{
        super.setUp();
        prepareOrders();
    }

    private void prepareOrders() {
        Patient patient = Context.getPatientService().getAllPatients().get(2);
        ivFluidOrder = ebolaTestData.createOrder(patient, IV_FLUID_ORDER_TYPE_UUID, "15f83cd6-64e9-4e06-a5f9-364d3b14a43d", false);
    }

    @Test
    public void testShouldGetIvFluidOrderStatusAsNotStartedIfNoStatusFound() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addParameter("order_uuid", ivFluidOrder.getUuid());
        request.addHeader("content-type", "application/json");
        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);

        LinkedHashMap order = (LinkedHashMap)responseObject.get("ivfluid-order");
        Assert.assertEquals(order.get("uuid"), ivFluidOrder.getUuid());
        Assert.assertEquals(order.get("status"), IvFluidOrderStatus.IVFluidOrderStatus.NOT_STARTED.toString());
    }

    @Test
    public void testShouldGetIvFluidOrderStatusAsStarted() throws Exception{
        ivFluidOrderStatusService.saveStatus(ivFluidOrder, IvFluidOrderStatus.IVFluidOrderStatus.STARTED);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addParameter("order_uuid", ivFluidOrder.getUuid());
        request.addHeader("content-type", "application/json");
        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);

        LinkedHashMap order = (LinkedHashMap)responseObject.get("ivfluid-order");
        Assert.assertEquals(order.get("uuid"), ivFluidOrder.getUuid());
        Assert.assertEquals(order.get("status"), IvFluidOrderStatus.IVFluidOrderStatus.STARTED.toString());
    }


    @Test
    public void testShouldSaveIvFluidOrderStatusAsStarted() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", requestURI);
        request.setContentType(MediaType.APPLICATION_JSON.toString());
        request.addHeader("content-type", "application/json");
        String content = "{\"order_uuid\": \"" + ivFluidOrder.getUuid() + "\", \"status\": \"STARTED\"}";

        request.setContent(content.getBytes());
        webMethods.handle(request);

        IvFluidOrderStatus currentStatus = ivFluidOrderStatusService.getCurrentStatus(ivFluidOrder);
        Assert.assertEquals(IvFluidOrderStatus.IVFluidOrderStatus.STARTED, currentStatus.getStatus());

    }
}