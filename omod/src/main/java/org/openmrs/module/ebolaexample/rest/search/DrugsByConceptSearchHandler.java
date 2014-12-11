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

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Allows finding a drug by mapping
 */
@Component
public class DrugsByConceptSearchHandler implements SearchHandler {

    public static final String REQUEST_PARAM_CONCEPT = "concept";

    @Autowired
    @Qualifier("conceptService")
    ConceptService conceptService;

    SearchQuery searchQuery = new SearchQuery.Builder(
            "Allows you to find drugs based matching a generic concept")
            .withRequiredParameters(REQUEST_PARAM_CONCEPT).build();

    private final SearchConfig searchConfig = new SearchConfig("getDrugsByConcept", RestConstants.VERSION_1 + "/drug",
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
        String conceptUUID = context.getParameter(REQUEST_PARAM_CONCEPT);
        Concept concept = Context.getConceptService().getConceptByUuid(conceptUUID);
        List<Drug> drugsByConcept = Context.getConceptService().getDrugsByConcept(concept);

        return new NeedsPaging<Drug>(drugsByConcept, context);
    }
}