package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.OrderType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.OrderService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = { OrderType.class })
public class OrderTypeDeployHandler extends AbstractObjectDeployHandler<OrderType> {

    @Autowired
    @Qualifier("orderService")
    private OrderService orderService;

    @Override
    public OrderType fetch(String uuid) {
        return orderService.getOrderTypeByUuid(uuid);
    }

    @Override
    public OrderType save(OrderType orderType) {
        return orderService.saveOrderType(orderType);
    }

    @Override
    public void uninstall(OrderType orderType, String reason) {
        orderService.retireOrderType(orderType, reason);
    }
}
