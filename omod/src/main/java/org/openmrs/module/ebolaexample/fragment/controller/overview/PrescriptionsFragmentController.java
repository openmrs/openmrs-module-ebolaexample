package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.Concept;
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
import java.util.Calendar;
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
                           @SpringBean("orderService") OrderService orderService,
                           FragmentModel model) {
        OrderType orderType = orderService.getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);

        List<Order> orders = getActiveAndRecentOrders(orderService, patient.getPatient(), orderType);

        DrugGroupMapping groups = new DrugGroupMapping();
        for (Order order : orders) {
            groups.add((DrugOrder) order);
        }
        model.put("groupedOrders", groups.getAsList());

        Set<DrugOrder> recentOrders = new HashSet<DrugOrder>();
        Calendar temp = Calendar.getInstance();
        temp.add(Calendar.HOUR_OF_DAY, -24);
        Date cutoff = temp.getTime();
        for (Order order : orders) {
            DrugOrder drugOrder = (DrugOrder) order;
//            Date changed = mostRecentChange(drugOrder);
            Date changed = drugOrder.getDateActivated();
            if (changed.after(cutoff)) {
                recentOrders.add(drugOrder);
            }
        }
        model.put("recentOrders", recentOrders);
    }

    private List<Order> getActiveAndRecentOrders(OrderService orderService, Patient patient, OrderType orderType) {
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
                // since as far as OpenMRS 1.10.1 the "isActive" method is wrong (it doesn't include active orders
                // scheduled for the future) we avoid using it.
                Date endDate = candidate.getEffectiveStopDate();
                return endDate == null || endDate.after(cutoff);
            }
        });
        return orders;
    }

    class ConceptAndRoute {
        private Concept concept;
        private Concept route;

        ConceptAndRoute(Concept concept, Concept route) {
            this.concept = concept;
            this.route = route;
        }

        @Override
        public int hashCode() {
            return (concept == null ? 1 : concept.hashCode()) + (route == null ? 1 : route.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ConceptAndRoute)) {
                return false;
            }
            ConceptAndRoute other = (ConceptAndRoute) o;
            return OpenmrsUtil.nullSafeEquals(concept, other.concept) && OpenmrsUtil.nullSafeEquals(route, other.route);
        }

        public Concept getConcept() {
            return concept;
        }

        public void setConcept(Concept concept) {
            this.concept = concept;
        }

        public Concept getRoute() {
            return route;
        }

        public void setRoute(Concept route) {
            this.route = route;
        }
    }

    class DrugGroupMapping extends HashMap<ConceptAndRoute, List<DrugOrder>> {

        public void add(DrugOrder o) {
            ConceptAndRoute key = new ConceptAndRoute(o.getConcept(), o.getRoute());
            if (!containsKey(key)) {
                put(key, new ArrayList<DrugOrder>());
            }
            get(key).add(o);
        }

        public List<Map.Entry<ConceptAndRoute, List<DrugOrder>>> getAsList() {
            List<Map.Entry<ConceptAndRoute, List<DrugOrder>>> list = new ArrayList<Map.Entry<ConceptAndRoute, List<DrugOrder>>>(this.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<ConceptAndRoute, List<DrugOrder>>>() {
                @Override
                public int compare(Map.Entry<ConceptAndRoute, List<DrugOrder>> left, Map.Entry<ConceptAndRoute, List<DrugOrder>> right) {
                    return -OpenmrsUtil.compareWithNullAsEarliest(mostRecentChange(left.getValue()), mostRecentChange(right.getValue()));
                }
            });
            for (Map.Entry<ConceptAndRoute, List<DrugOrder>> entry : list) {
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
            mostRecent = max(mostRecent, drugOrder.getDateActivated(), drugOrder.getDateStopped(), drugOrder.getDateChanged());
        }
        return mostRecent;
    }

    private Date mostRecentChange(DrugOrder order) {
        return max(order.getDateActivated(), order.getDateStopped(), order.getDateChanged());
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
