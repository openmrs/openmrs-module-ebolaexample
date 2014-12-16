package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.ArrayList;
import java.util.List;

public class NewAndUpdatedPrescriptionsFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient, FragmentModel model) {

        OrderType orderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

        List<Order> orders = Context.getOrderService().getActiveOrders(patient.getPatient(), orderType, null, null);

        List<DrugOrder> newUpdatedDrugOrders = new ArrayList<DrugOrder>();
        for (Order order : orders) {
            // if(order.)
            newUpdatedDrugOrders.add((DrugOrder) order);
        }
        model.addAttribute("newUpdatedDrugOrders", newUpdatedDrugOrders);
    }
}
