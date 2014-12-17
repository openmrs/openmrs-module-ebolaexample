package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.DateUtil;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PastPrescriptionsFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient, FragmentModel model) {

        OrderType orderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

        List<Order> orders = Context.getOrderService().getActiveOrders(patient.getPatient(), orderType, null, null);

        List<DrugOrder> pastDrugOrders = new ArrayList<DrugOrder>();
        for (Order order : orders) {
            if(!DateUtil.isInLast24Hours(order.getDateCreated())) {
                pastDrugOrders.add((DrugOrder) order);
            }
        }
        model.addAttribute("pastDrugOrders", pastDrugOrders);
    }

    }
