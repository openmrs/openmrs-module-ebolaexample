package org.openmrs.module.ebolaexample.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.*;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.DateUtil;
import org.openmrs.module.ebolaexample.EbolaPatient;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EbolaPharmacyPageController {

    public void get(@SpringBean("patientService") PatientService patientService,
                    @RequestParam(value = "ward", required = false) String wardId, PageModel model) {
        List<EbolaPatient> ebolaPatients = new ArrayList<EbolaPatient>();
        List<Patient> patients = patientService.getAllPatients();

        Location ward = null;
        if(StringUtils.isNotBlank(wardId)){
            ward = Context.getLocationService().getLocationByUuid(wardId);
        }
        OrderType orderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

        List<Location> locations = new ArrayList<Location>();

        for (Patient patient : patients) {

            List<Order> orders = Context.getOrderService().getActiveOrders(patient, orderType, null, null);
            EbolaPatient ebolaPatient = new EbolaPatient(patient);

            List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();
            for (Order order : orders) {
                if (DateUtil.isInLast24Hours(order.getDateCreated())) {
                    drugOrders.add((DrugOrder) order);
                }
            }
            if (drugOrders != null && drugOrders.size() > 0) {
                PatientIdentifier identifier = null;
                for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
                    identifier = patientIdentifier;
                    break;
                }

                locations.add(identifier.getLocation());

                if(ward==null || ward!=null && ward.equals(identifier.getLocation())) {
                    ebolaPatient.setDrugOrders(drugOrders);
                    ebolaPatients.add(ebolaPatient);
                }
            }
        }

        model.addAttribute("ebolaPatients", ebolaPatients);
        model.addAttribute("wards", locations);
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
