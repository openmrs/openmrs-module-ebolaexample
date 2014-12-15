package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.EbolaPatient;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EbolaPharmacyPageController {

    public void get(@SpringBean("patientService") PatientService patientService, PageModel model) {
        List<EbolaPatient> ebolaPatients = new ArrayList<EbolaPatient>();
        List<Patient> patients = patientService.getAllPatients();

        for (Patient patient : patients) {
            OrderType orderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
            List<Order> orders = Context.getOrderService().getActiveOrders(patient, orderType, null, null);
            System.out.println(patient + " has " + orders.size() + " active orders");
            EbolaPatient ebolaPatient = new EbolaPatient(patient);

            List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();
            for (Order order : orders) {
                drugOrders.add((DrugOrder) order);
            }
            ebolaPatient.setDrugOrders(drugOrders);
            ebolaPatients.add(ebolaPatient);
        }
        model.addAttribute("ebolaPatients", ebolaPatients);
        model.addAttribute("today", getDateToday());
    }

    private Date getDateToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
