package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.SimpleDosingInstructions;
import org.openmrs.api.OrderService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.DateUtil;
import org.openmrs.module.ebolaexample.page.controller.ChangeInPatientLocationPageController;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.Formatter;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

import java.util.*;

public class PrescriptionsFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @FragmentParam(value = "showAll", defaultValue = "false") Boolean showAll,
                           @SpringBean("orderService") OrderService orderService,
                           @SpringBean AdtService adtService,
                           final UiUtils ui,
                           UiSessionContext sessionContext,
                           FragmentModel model) {

        OrderType orderType = orderService.getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

        boolean onlyRecent = !showAll;
        List<Order> orders = getOrders(orderService, patient.getPatient(), orderType, onlyRecent);

        DrugGroupMapping groups = new DrugGroupMapping();
        for (Order order : orders) {
            groups.add((DrugOrder) order);
        }
        List<Map.Entry<ConceptAndDrug, List<DrugOrder>>> groupList = groups.getAsList();

        applyFlag(groupList, "inactive", new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                List<DrugOrder> orders = (List<DrugOrder>) o;
                boolean anyActive = false;
                for (DrugOrder order : orders) {
                    if (order.isActive()) {
                        anyActive = true;
                    }
                }
                return !anyActive;
            }
        });

        final Date cutoff = new DateTime().minusHours(24).toDate();
        applyFlag(groupList, "recent", new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                List<DrugOrder> orders = (List<DrugOrder>) o;
                Date changed = DateUtil.mostRecentChange(orders.get(0));
                return changed.after(cutoff);
            }
        });

        model.put("groupedOrders", groupList);
        model.put("showAll", showAll);
        model.put("prescriptionFormatter", new Formatter() {
            @Override
            public String format(Object o, Locale locale) {
                DrugOrder order = (DrugOrder) o;

                // we need custom formatting for SimpleDosingInstructions since that core class doesn't know to use the
                // Ebola-preferred concept names (e.g. it shows "Milliliter" instead of "mL")
                if (order.getDosingType().equals(SimpleDosingInstructions.class)) {
                    SimpleDosingInstructions instructions = (SimpleDosingInstructions) order.getDosingInstructionsInstance();
                    // copied from SimpleDosingInstructions in core, and just changes the formatting of concepts
                    StringBuilder dosingInstructions = new StringBuilder();
                    dosingInstructions.append(instructions.getDose());
                    dosingInstructions.append(" ");
                    dosingInstructions.append(ui.format(instructions.getDoseUnits()));
                    dosingInstructions.append(" ");
                    dosingInstructions.append(ui.format(instructions.getRoute()));
                    dosingInstructions.append(" ");
                    dosingInstructions.append(ui.format(instructions.getFrequency().getConcept()));
                    if (order.getDuration() != null) {
                        dosingInstructions.append(" ");
                        dosingInstructions.append(instructions.getDuration());
                        dosingInstructions.append(" ");
                        dosingInstructions.append(ui.format(instructions.getDurationUnits()));
                    }
                    if (order.getAsNeeded()) {
                        dosingInstructions.append(" ");
                        dosingInstructions.append("PRN");
                        if (order.getAsNeededCondition() != null) {
                            dosingInstructions.append(" ");
                            dosingInstructions.append(instructions.getAsNeededCondition());
                        }
                    }
                    if (instructions.getAdministrationInstructions() != null) {
                        dosingInstructions.append(" ");
                        dosingInstructions.append(instructions.getAdministrationInstructions());
                    }
                    return dosingInstructions.toString();
                } else {
                    return order.getDosingInstructionsInstance().getDosingInstructionsAsString(locale);
                }
            }
        });

        VisitDomainWrapper activeVisit = ChangeInPatientLocationPageController.getActiveVisit(patient.getPatient(), adtService, sessionContext);
        InpatientLocationFragmentController inpatientLocationFragmentController = new InpatientLocationFragmentController();
        inpatientLocationFragmentController.controller(activeVisit, model);
    }

    private void applyFlag(List<Map.Entry<ConceptAndDrug, List<DrugOrder>>> groupList, String flag, Predicate predicate) {
        for (Map.Entry<ConceptAndDrug, List<DrugOrder>> entry : groupList) {
            if (predicate.evaluate(entry.getValue())) {
                entry.getKey().addFlag(flag);
            }
        }
    }

    private List<Order> getOrders(OrderService orderService, Patient patient, OrderType orderType, final boolean onlyRecent) {
        final Date cutoff = new Date(); //new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 24 hours ago
        List<Order> orders = orderService.getAllOrdersByPatient(patient);
        CollectionUtils.filter(orders, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                if (!(o instanceof DrugOrder)) {
                    return false;
                }
                DrugOrder candidate = (DrugOrder) o;
                if (candidate.isVoided() || candidate.getAction() == Order.Action.DISCONTINUE) {
                    return false;
                }
                if (onlyRecent) {
                    // since as far as OpenMRS 1.10.1 the "isActive" method is wrong (it doesn't include active orders
                    // scheduled for the future) we avoid using it.
                    Date endDate = candidate.getEffectiveStopDate();
                    return endDate == null || endDate.after(cutoff);
                } else {
                    return true;
                }
            }
        });
        return orders;
    }

    class ConceptAndDrug {
        private Concept concept;
        private Drug drug;

        private transient Set<String> flags = new HashSet<String>();

        ConceptAndDrug(Concept concept, Drug drug) {
            this.concept = concept;
            this.drug = drug;
        }

        public void addFlag(String flag) {
            flags.add(flag);
        }

        @Override
        public int hashCode() {
            return concept.hashCode() * 13 + (drug == null ? 0 : drug.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ConceptAndDrug)) {
                return false;
            }
            ConceptAndDrug other = (ConceptAndDrug) o;
            return OpenmrsUtil.nullSafeEquals(concept, other.concept) && OpenmrsUtil.nullSafeEquals(drug, other.drug);
        }

        public Concept getConcept() {
            return concept;
        }

        public void setConcept(Concept concept) {
            this.concept = concept;
        }

        public Drug getDrug() {
            return drug;
        }

        public void setDrug(Drug drug) {
            this.drug = drug;
        }

        public Set<String> getFlags() {
            return flags;
        }
    }

    class DrugGroupMapping extends HashMap<ConceptAndDrug, List<DrugOrder>> {

        public void add(DrugOrder o) {
            ConceptAndDrug key = new ConceptAndDrug(o.getConcept(), o.getDrug());
            if (!containsKey(key)) {
                put(key, new ArrayList<DrugOrder>());
            }
            get(key).add(o);
        }

        public List<Map.Entry<ConceptAndDrug, List<DrugOrder>>> getAsList() {
            List<Map.Entry<ConceptAndDrug, List<DrugOrder>>> list = new ArrayList<Map.Entry<ConceptAndDrug, List<DrugOrder>>>(this.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<ConceptAndDrug, List<DrugOrder>>>() {
                @Override
                public int compare(Map.Entry<ConceptAndDrug, List<DrugOrder>> left, Map.Entry<ConceptAndDrug, List<DrugOrder>> right) {
                    return -OpenmrsUtil.compareWithNullAsEarliest(DateUtil.mostRecentChange(left.getValue()), DateUtil.mostRecentChange(right.getValue()));
                }
            });
            for (Map.Entry<ConceptAndDrug, List<DrugOrder>> entry : list) {
                Collections.sort(entry.getValue(), new Comparator<DrugOrder>() {
                    @Override
                    public int compare(DrugOrder left, DrugOrder right) {
                        return right.getEffectiveStartDate().compareTo(left.getEffectiveStartDate());
                    }
                });
            }

            return list;
        }
    }

}
