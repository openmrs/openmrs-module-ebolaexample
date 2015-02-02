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
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.importer.DrugImporter;
import org.openmrs.module.ebolaexample.importer.TierDrug;
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
public class DrugConceptSearchHandler implements SearchHandler {

    public static final String REQUEST_PARAM_FORMULARY = "formulary";
    public static final String REQUEST_PARAM_QUERY = "q";

    @Autowired
    @Qualifier("conceptService")
    ConceptService conceptService;

    @Autowired
    private DrugImporter drugImporter;

    SearchQuery searchQuery = new SearchQuery.Builder("Allows you to find active drug concept names")
            .withRequiredParameters(REQUEST_PARAM_FORMULARY).withOptionalParameters(REQUEST_PARAM_QUERY).build();

    private final SearchConfig searchConfig = new SearchConfig("getDrugConcepts", RestConstants.VERSION_1 + "/concept",
            Arrays.asList("1.10.*", "1.11.*"), searchQuery);

    private Comparator<? super Concept> compareByDisplay = new Comparator<Concept>() {
        @Override
        public int compare(Concept o1, Concept o2) {
            return o2.getDisplayString().compareToIgnoreCase(o1.getDisplayString());
        }
    };

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
        String query = context.getParameter(REQUEST_PARAM_QUERY);
        List<Drug> allDrugs = new ArrayList<Drug>();
        if (query == null || query.isEmpty()) {
            allDrugs = Context.getConceptService().getAllDrugs(context.getIncludeAll());
        } else {
            allDrugs = Context.getConceptService().getDrugs(query, Context.getLocale(), false, false);
        }

        List<TierDrug> tierDrugs = drugImporter.getTierDrugs();
        for (Drug drug : allDrugs) {
            for (TierDrug tierDrug : tierDrugs) {
                if (tierDrug.getUuid().equalsIgnoreCase(drug.getUuid())) {
                    tierDrug.setDrug(drug);
                    break;
                }
            }
        }

        List<TierDrug> tierDrugList = new ArrayList<TierDrug>();

        for (TierDrug tierDrug : tierDrugs) {
            if (tierDrug.getDrug() != null) {
                tierDrugList.add(tierDrug);
            }
        }

        Collections.sort(tierDrugList, compareByTierAndDrugDisplay);
        int startIndex = 0;
        int endIndex = getIndexTierDrugWith20thConcept(tierDrugList);
        List<TierDrug> tier1Drugs = tierDrugList.subList(startIndex, endIndex);
        Collections.sort(tier1Drugs, compareByDrugDisplay);

        tier1Drugs.addAll(tierDrugList.subList(endIndex, tierDrugList.size()));

        Set<Concept> drugNames = new LinkedHashSet<Concept>();
        for (TierDrug tierDrug : tier1Drugs) {
            drugNames.add(tierDrug.getDrug().getConcept());
        }

        if (drugNames.size() == 0) {
            return new EmptySearchResult();
        }
        ArrayList<Concept> concepts = new ArrayList<Concept>(drugNames);

        return new NeedsPaging<Concept>(concepts, context);
    }

    private int getIndexTierDrugWith20thConcept(List<TierDrug> tierDrugs) {
        List<Concept> drugNames = new ArrayList<Concept>();
        int i = 0;
        for (; i < tierDrugs.size() && drugNames.size() < 20; i++) {
            if (!drugNames.contains(tierDrugs.get(i).getDrug().getConcept())) {
                drugNames.add(tierDrugs.get(i).getDrug().getConcept());
            }
        }
        return drugNames.size() == 20 ? i : tierDrugs.size();
    }

    public static Comparator<? super TierDrug> compareByTierAndDrugDisplay = new Comparator<TierDrug>() {
        @Override
        public int compare(TierDrug o1, TierDrug o2) {
            if (o1.getTier().equalsIgnoreCase(o2.getTier())) {
                return o1.getDrug().getConcept().getDisplayString().compareToIgnoreCase(o2.getDrug().getConcept().getDisplayString());
            } else {
                return o1.getTier().compareToIgnoreCase(o2.getTier());
            }
        }
    };

    public static Comparator<? super TierDrug> compareByDrugDisplay = new Comparator<TierDrug>() {
        @Override
        public int compare(TierDrug o1, TierDrug o2) {
            return o1.getDrug().getConcept().getDisplayString().compareToIgnoreCase(o2.getDrug().getConcept().getDisplayString());
        }
    };

}