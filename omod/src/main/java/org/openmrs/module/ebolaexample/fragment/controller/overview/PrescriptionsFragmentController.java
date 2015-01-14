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
import org.openmrs.api.OrderService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrescriptionsFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @FragmentParam(value = "showAll", defaultValue = "false") Boolean showAll,
                           @SpringBean("orderService") OrderService orderService,
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
                Date changed = mostRecentChange(orders.get(0));
                return changed.after(cutoff);
            }
        });

        model.put("groupedOrders", groupList);
        model.put("showAll", showAll);
    }

    private void applyFlag(List<Map.Entry<ConceptAndDrug, List<DrugOrder>>> groupList, String flag, Predicate predicate) {
        for (Map.Entry<ConceptAndDrug, List<DrugOrder>> entry : groupList) {
            if (predicate.evaluate(entry.getValue())) {
                entry.getKey().addFlag(flag);
            }
        }
    }

    private List<Order> getOrders(OrderService orderService, Patient patient, OrderType orderType, final boolean onlyRecent) {
        final Date cutoff = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 24 hours ago
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
                }
                else {
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
                    return -OpenmrsUtil.compareWithNullAsEarliest(mostRecentChange(left.getValue()), mostRecentChange(right.getValue()));
                }
            });
            for (Map.Entry<ConceptAndDrug, List<DrugOrder>> entry : list) {
                Collections.sort(entry.getValue(), new Comparator<DrugOrder>() {
                    @Override
                    public int compare(DrugOrder left, DrugOrder right) {
                        return mostRecentChange(right).compareTo(mostRecentChange(left));
                    }
                });
            }

            return list;
        }
    }

    private Date mostRecentChange(List<DrugOrder> list) {
        Date mostRecent = null;
        for (DrugOrder drugOrder : list) {
            mostRecent = max(mostRecent, drugOrder.getDateActivated(), actualStopDate(drugOrder), drugOrder.getDateChanged());
        }
        return mostRecent;
    }

    private Date mostRecentChange(DrugOrder order) {
        return max(order.getDateActivated(), actualStopDate(order), order.getDateChanged());
    }

    private Date actualStopDate(DrugOrder order) {
        if (order.getDateStopped() != null) {
            return order.getDateStopped();
        }
        if (order.getAutoExpireDate() != null && OpenmrsUtil.compare(order.getAutoExpireDate(), new Date()) < 0) {
            return order.getAutoExpireDate();
        }
        return null;
    }

    private Date max(Date... dates) {
        Date max = null;
        for (Date date : dates) {
            if (date == null) {
                continue;
            }
            if (max == null || date.compareTo(max) > 0) {
                max = date;
            }
        }
        return max;
    }

}
