/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ebolaexample.rest.search;

import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows finding a drug by mapping
 */
@Component
public class ExpiredDrugOrdersByPatientHandler implements SearchHandler {

    public static final String REQUEST_PARAM_PATIENT = "patient";
    public static final String REQUEST_PARAM_EXPIRED = "expired";

    SearchQuery searchQuery = new SearchQuery.Builder(
            "Allows you to find expired drug orders by patient")
            .withRequiredParameters(REQUEST_PARAM_PATIENT, REQUEST_PARAM_EXPIRED).build();

    private final SearchConfig searchConfig = new SearchConfig("getExpiredDrugOrdersForPatient", RestConstants.VERSION_1 + "/order",
            Arrays.asList("1.10.*", "1.11.*"), searchQuery);

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     */
    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#search(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        String uuid = context.getParameter(REQUEST_PARAM_PATIENT);
        Patient patient = Context.getPatientService().getPatientByUuid(uuid);
        List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);

        List<DrugOrder> expiredDrugOrders = new ArrayList<DrugOrder>();
        OrderType drugOrderType = Context.getOrderService().getOrderTypeByUuid(OrderType.DRUG_ORDER_TYPE_UUID);
        for (Order order : orders) {
            if(order.isType(drugOrderType) && !order.isActive()) {
                expiredDrugOrders.add((DrugOrder) order);
            }
        }

        return new NeedsPaging<DrugOrder>(expiredDrugOrders, context);
    }
}