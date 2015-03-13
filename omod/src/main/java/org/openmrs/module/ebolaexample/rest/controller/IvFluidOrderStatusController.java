package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.module.ebolaexample.api.IvFluidOrderStatusService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/ivfluid-order-status")
public class IvFluidOrderStatusController {
    @Autowired
    IvFluidOrderStatusService ivFluidOrderStatusService;

}
