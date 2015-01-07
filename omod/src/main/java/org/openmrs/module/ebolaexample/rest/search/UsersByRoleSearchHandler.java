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

import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows finding a drug by mapping
 */
@Component
public class UsersByRoleSearchHandler implements SearchHandler {

    public static final String REQUEST_PARAM_ROLE = "role";

    SearchQuery searchQuery = new SearchQuery.Builder(
            "Allows you to find users by role")
            .withRequiredParameters(REQUEST_PARAM_ROLE).build();

    private final SearchConfig searchConfig = new SearchConfig("getUsersByRole", RestConstants.VERSION_1 + "/user",
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
        String roleDescription = context.getParameter(REQUEST_PARAM_ROLE);
        Role role = Context.getUserService().getRole(roleDescription);
        List<User> usersByRole = Context.getUserService().getUsersByRole(role);
        ArrayList<UserAndPassword1_8> pageableUsers = new ArrayList<UserAndPassword1_8>();
        for (User user : usersByRole) {
            pageableUsers.add(new UserAndPassword1_8(user));
        }
        return new NeedsPaging<UserAndPassword1_8>(pageableUsers, context);
    }
}