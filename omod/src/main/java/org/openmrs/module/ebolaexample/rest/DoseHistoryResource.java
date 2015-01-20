package org.openmrs.module.ebolaexample.rest;

import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.ebolaexample.pharmacy.DoseHistory;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Resource(name = RestConstants.VERSION_1 + "/ebola/dosehistory", supportedClass = DoseHistory.class, supportedOpenmrsVersions = "1.10.*")
public class DoseHistoryResource extends ReadableDelegatingResource<DoseHistory> {

    @Override
    public DoseHistory getByUniqueId(String uniqueId) {
        Patient patient = (Patient) ConversionUtil.convert(uniqueId, Patient.class);
        return Context.getService(PharmacyService.class).getScheduledDosesByPatientAndDateRange(patient, null, null);
    }

    @Override
    public DoseHistory newDelegate() {
        // return new DoseHistory(new ArrayList<DrugOrder>(), new ArrayList<ScheduledDose>());
        throw new UnsupportedOperationException();
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("dosesByOrder");
        return description;
    }

    // this is more correct but it requires adding a converter for OrderAndDoses
//    @PropertyGetter("dosesByOrder")
//    public List<OrderAndDoses> getDosesByOrder(DoseHistory doseHistory) {
//        Map<DrugOrder, List<ScheduledDose>> byOrder = doseHistory.getDosesByOrder();
//        List<OrderAndDoses> simple = new ArrayList<OrderAndDoses>();
//        for (Map.Entry<DrugOrder, List<ScheduledDose>> entry : byOrder.entrySet()) {
//            simple.add(new OrderAndDoses(entry.getKey(), entry.getValue()));
//        }
//        return simple;
//    }

    // this hack instead
    @PropertyGetter("dosesByOrder")
    public List<SimpleObject> getDosesByOrder(DoseHistory doseHistory) {
        Map<DrugOrder, List<ScheduledDose>> byOrder = doseHistory.getDosesByOrder();
        List<SimpleObject> list = new ArrayList<SimpleObject>();
        for (Map.Entry<DrugOrder, List<ScheduledDose>> entry : byOrder.entrySet()) {
            SimpleObject simple = new SimpleObject();
            simple.add("order", ConversionUtil.convertToRepresentation(entry.getKey(), Representation.DEFAULT));
            simple.add("doses", ConversionUtil.convertToRepresentation(entry.getValue(), Representation.DEFAULT));
            list.add(simple);
        }
        return list;
    }

}
