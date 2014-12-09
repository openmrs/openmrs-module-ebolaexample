package org.openmrs.module.ebolaexample.rest;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptResource1_9;
import org.openmrs.util.LocaleUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Resource(name = RestConstants.VERSION_1 + "/drug-concept", supportedClass = Concept.class, supportedOpenmrsVersions = "1.10.*")
public class DrugConceptResource extends ConceptResource1_9 {

    @Override
    protected PageableResult doSearch(RequestContext context) {
        ConceptService service = Context.getConceptService();
        Integer startIndex = null;
        Integer limit = null;
        boolean canPage = true;

        // Only set startIndex and limit if we can return paged results
        if (canPage) {
            startIndex = context.getStartIndex();
            limit = context.getLimit();
        }

        List<ConceptSearchResult> searchResults;

        // get the user's locales...and then convert that from a set to a list
        List<Locale> locales = new ArrayList<Locale>(LocaleUtility.getLocalesInOrder());

        List<ConceptClass> requiredClasses = new ArrayList<ConceptClass>();
        requiredClasses.add(Context.getConceptService().getConceptClassByUuid(ConceptClass.DRUG_UUID));

        searchResults = service.getConcepts(context.getParameter("q"), locales, context.getIncludeAll(), requiredClasses, null, null,
                null, null, startIndex, limit);

        // convert search results into list of concepts
        List<Concept> results = new ArrayList<Concept>(searchResults.size());
        for (ConceptSearchResult csr : searchResults) {
            results.add(csr.getConcept());
        }

        PageableResult result = null;
        if (canPage) {
            Integer count = service.getCountOfConcepts(context.getParameter("q"), locales, false, requiredClasses,
                    Collections.<ConceptClass> emptyList(), Collections
                    .<ConceptDatatype> emptyList(), Collections.<ConceptDatatype> emptyList(), null);
            boolean hasMore = count > startIndex + limit;
            result = new AlreadyPaged<Concept>(context, results, hasMore);
        } else {
            result = new NeedsPaging<Concept>(results, context);
        }

        return result;
    }
}
