package org.openmrs.module.ebolaexample.rest.search;


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.WardResource;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

/**
 * Integration tests for the DrugConceptSearchHandler
 */
public class DrugsByConceptSearchHandlerTest extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    private WardResource resource;
    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        response = new MockHttpServletResponse();
        requestURI = "concept";
    }

    @Test
    public void shouldReturnOnlyDrugConcepts() throws Exception {
        Drug firstDrug = Context.getConceptService().getAllDrugs().get(0);
        String conceptUUID = firstDrug.getConcept().getUuid();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rest/v1/drug");
        request.addParameter("concept",conceptUUID);
        request.addHeader("content-type", "application/json");

        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        List<LinkedHashMap> results = (List<LinkedHashMap>) responseObject.get("results");
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).get("display").equals(firstDrug.getDisplayName()));
    }
}