package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.db.ScheduledDoseDAO;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.ebolaexample.rest.ScheduledDoseResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.OrderResource1_10;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderResource1_9;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/drug-dose-history")
class DrugDoseHistoryController extends BaseRestController {


    @ResponseBody
    @RequestMapping("/{patientUuid}")
    public Object get(@PathVariable(value="patientUuid") String patientUuid) throws Exception {

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        List<Order> allOrdersByPatient = Context.getOrderService().getAllOrdersByPatient(patient);
        List<SimpleObject> doseHistories = new ArrayList<SimpleObject>();
        for (Order order : allOrdersByPatient) {
            if(order.isActive()) {
                SimpleObject doseHistoryRepresentation = buildDoseHistoryRepresentation(order);
                doseHistories.add(doseHistoryRepresentation);
            }
        }
        SimpleObject response = new SimpleObject();
        response.add("results", doseHistories);
        return response;
    }

    private SimpleObject buildDoseHistoryRepresentation(Order order) throws Exception {
        PharmacyService pharmacyService = Context.getService(PharmacyService.class);;
        SimpleObject orderRef = orderRef(order);
        List<ScheduledDose> scheduledDosesForOrder = pharmacyService.getScheduledDosesForOrder(order);
        List<SimpleObject> doseRefs = new ArrayList<SimpleObject>();
        for (ScheduledDose scheduledDose : scheduledDosesForOrder) {
            SimpleObject doseRef = new ScheduledDoseResource().asRepresentation(scheduledDose, Representation.DEFAULT);
            doseRefs.add(doseRef);
        }
        orderRef.add("doses", doseRefs);
        return orderRef;
    }

    private SimpleObject orderRef(Order order) throws Exception {
        OrderResource1_10 orderResource1_10 = new OrderResource1_10();
        return orderResource1_10.asDefaultRep(order);
    }
}
