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
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_10;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.DrugResource1_10;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Allows finding a drug by mapping
 */
@Component
public class DrugConceptSearchHandler implements SearchHandler {

    public static final String REQUEST_PARAM_FORMULARY = "formulary";

    @Autowired
    @Qualifier("conceptService")
    ConceptService conceptService;

    SearchQuery searchQuery = new SearchQuery.Builder(
            "Allows you to find active drug concept names")
            .withRequiredParameters(REQUEST_PARAM_FORMULARY).build();

    private final SearchConfig searchConfig = new SearchConfig("getDrugConcepts", RestConstants.VERSION_1 + "/concept",
            Arrays.asList("1.10.*", "1.11.*"), searchQuery);

    private Comparator<? super Concept> compareByDisplay = new Comparator<Concept>() {
        @Override
        public int compare(Concept o1, Concept o2) {
            return o1.getDisplayString().compareToIgnoreCase(o2.getDisplayString());
        }
    };;

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
        List<Drug> allDrugs = Context.getConceptService().getAllDrugs(context.getIncludeAll());
        Set<Concept> drugNames = new HashSet<Concept>();
        for (Drug drug: allDrugs) {
            drugNames.add(drug.getConcept());
        }

        if (drugNames.size() == 0) {
            return new EmptySearchResult();
        }
        ArrayList<Concept> concepts = new ArrayList<Concept>(drugNames);
        Collections.sort(concepts, compareByDisplay);
        return new NeedsPaging<Concept>(concepts, context);
    }
}