package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.Order;
import org.openmrs.api.OrderService;
import org.openmrs.module.ebolaexample.api.IvFluidOrderStatusService;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.domain.IvFluidOrderStatus;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/ivfluid-order-status")
public class IvFluidOrderStatusController {
    @Autowired
    IvFluidOrderStatusService ivFluidOrderStatusService;

    @Autowired
    OrderService orderService;
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object get(@RequestParam(value="order_uuid", required=false) String orderUuid) throws Exception {
        SimpleObject response = new SimpleObject();
        Order ivFluidOrder = orderService.getOrderByUuid(orderUuid);
        if(ivFluidOrder == null || !ivFluidOrder.getOrderType().getUuid().equals(EbolaMetadata._OrderType.IV_FLUID_ORDER_TYPE_UUID)){
            throw new ResourceDoesNotSupportOperationException("This is not an IV fluid order");
        }

        IvFluidOrderStatus currentStatus = ivFluidOrderStatusService.getCurrentStatus((IvFluidOrder) ivFluidOrder);
        IvFluidOrderStatus.IVFluidOrderStatus status = currentStatus == null ?
                IvFluidOrderStatus.IVFluidOrderStatus.NOT_STARTED :
                currentStatus.getStatus();

        response.add("ivfluid-order",
                new SimpleObject().
                        add("uuid", orderUuid)
                        .add("status", status)
        );
        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Object post(@RequestBody Map<String, Object> map) throws Exception{
        SimpleObject response = new SimpleObject();
        String orderUuid = (String) map.get("order_uuid");
        IvFluidOrderStatus.IVFluidOrderStatus status = IvFluidOrderStatus.IVFluidOrderStatus.valueOf((String)map.get("status"));

        Order ivFluidOrder = orderService.getOrderByUuid(orderUuid);
        if(ivFluidOrder == null || ivFluidOrder.getOrderType().getUuid() != EbolaMetadata._OrderType.IV_FLUID_ORDER_TYPE_UUID){
            throw new ResourceDoesNotSupportOperationException("This is not an IV fluid order");
        }

        ivFluidOrderStatusService.saveStatus((IvFluidOrder)ivFluidOrder, status);
        return response;
    }

}
