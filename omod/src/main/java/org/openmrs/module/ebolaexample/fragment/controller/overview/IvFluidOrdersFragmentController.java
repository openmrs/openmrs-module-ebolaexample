package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.module.ebolaexample.FormatUtil;
import org.openmrs.module.ebolaexample.api.IvFluidOrderStatusService;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.domain.IvFluidOrderStatus;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class IvFluidOrdersFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @FragmentParam(value = "showAll", defaultValue = "false") Boolean showAll,
                           @SpringBean("orderService") OrderService orderService,
                           @SpringBean("ivFluidOrderStatusService") IvFluidOrderStatusService ivFluidOrderStatusService,
                           final UiUtils ui,
                           FragmentModel model) {

        List<OrderView> orders = getOrders(orderService, ivFluidOrderStatusService, patient.getPatient(), showAll);

        model.put("orders", orders);
        model.put("fluidOrderFormatter", new FormatUtil());
    }

    private List<OrderView> getOrders(OrderService orderService, IvFluidOrderStatusService ivFluidOrderStatusService,
                                      Patient patient, Boolean showAll) {
        List<Order> orders = orderService.getAllOrdersByPatient(patient);
        CollectionUtils.filter(orders, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                if (!(o instanceof IvFluidOrder)) {
                    return false;
                }
                IvFluidOrder candidate = (IvFluidOrder) o;
                if (candidate.isVoided() || candidate.getAction() == Order.Action.DISCONTINUE) {
                    return false;
                }
                return true;
            }
        });

        List<OrderView> result = new ArrayList<OrderView>();
        for (Order order : orders) {
            IvFluidOrderStatus status = ivFluidOrderStatusService.getCurrentStatus((IvFluidOrder) order);
            result.add(new OrderView((IvFluidOrder) order, status));
        }

        if (!showAll) {
            CollectionUtils.filter(result, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    IvFluidOrderStatus status = ((OrderView) o).getStatus();
                    return (status == null || status.getStatus() != IvFluidOrderStatus.IVFluidOrderStatus.STOPPED);
                }
            });
        }

        Collections.sort(result);

        return result;
    }

    class OrderView implements Comparable<OrderView> {
        private IvFluidOrder order;
        private IvFluidOrderStatus status;
        private String lastStatus;
        private Date lastStatusChange;

        public OrderView(IvFluidOrder order, IvFluidOrderStatus status) {
            this.order = order;
            this.status = status;
            if (status != null) {
                lastStatus = status.getStatus().toString().replace('_', ' ') + ": ";
                lastStatusChange = status.getDateCreated();
            } else {
                lastStatus = "NOT STARTED";
                lastStatusChange = order.getDateCreated();
            }
        }

        public IvFluidOrder getOrder() {
            return order;
        }

        public IvFluidOrderStatus getStatus() {
            return status;
        }

        public String getLastStatus() {
            return lastStatus;
        }

        public Date getLastStatusChange() {
            return lastStatusChange;
        }

        @Override
        public int compareTo(OrderView that) {
            return -OpenmrsUtil.compare(this.lastStatusChange, that.lastStatusChange);
        }
    }

}
