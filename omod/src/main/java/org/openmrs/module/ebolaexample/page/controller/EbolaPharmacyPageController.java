package org.openmrs.module.ebolaexample.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.DrugOrder;
import org.openmrs.Location;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.DateUtil;
import org.openmrs.module.ebolaexample.EbolaPatient;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.util.MetadataComparator;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.openmrs.module.ebolaexample.metadata.EbolaMetadata._VisitAttributeType.ASSIGNED_BED;
import static org.openmrs.module.ebolaexample.metadata.EbolaMetadata._VisitAttributeType.ASSIGNED_WARD;

public class EbolaPharmacyPageController {

    public void get(@SpringBean("patientService") PatientService patientService,
                    @RequestParam(value = "ward", required = false) String wardId, PageModel model) {
        List<EbolaPatient> ebolaPatients = new ArrayList<EbolaPatient>();
        List<Patient> patients = patientService.getAllPatients();

        Location ward = null;
        if (StringUtils.isNotBlank(wardId)) {
            ward = Context.getLocationService().getLocationByUuid(wardId);
        }
        model.put("selectedWard", ward);

        OrderType orderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

        Set<Location> locations = new TreeSet<Location>(new MetadataComparator(Context.getLocale()));

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

                Location patientWard = null;
                Location patientBed = null;

                List<Visit> visits = Context.getVisitService().getActiveVisitsByPatient(patient);
                VisitAttributeType assignedWardType = Context.getVisitService().getVisitAttributeTypeByUuid(ASSIGNED_WARD);
                VisitAttributeType assignedBedType = Context.getVisitService().getVisitAttributeTypeByUuid(ASSIGNED_BED);

                if (visits != null && visits.size() > 0) {
                    for (Visit visit : visits) {
                        if (visit.getVoided()) {
                            continue;
                        }
                        for (VisitAttribute va : visits.get(0).getAttributes()) {
                            if (va.getAttributeType().equals(assignedBedType)) {
                                patientBed = (Location) va.getValue();
                            }
                            if (va.getAttributeType().equals(assignedWardType)) {
                                patientWard = (Location) va.getValue();
                            }
                        }
                    }
                } else {
                    System.out.println("No visit found");
                }

                ebolaPatient.setBed(patientBed);
                ebolaPatient.setWard(patientWard);


                locations.add(patientWard);

                if (ward == null || (ward != null && patientWard != null && ward.equals(patientWard))) {
                    ebolaPatient.setDrugOrders(drugOrders);
                    ebolaPatients.add(ebolaPatient);
                }
            }
        }

        model.addAttribute("ebolaPatients", ebolaPatients);
        model.addAttribute("wards", locations);
        model.addAttribute("today", DateUtil.getDateToday());
    }
}
