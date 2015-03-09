package org.openmrs.module.ebolaexample.rest.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class PatientAssignmentControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        response = new MockHttpServletResponse();
        requestURI =  "/rest/" + RestConstants.VERSION_1 + "/ebola/assignment";
        setUpDurationUnits();
    }

    private void setUpDurationUnits() {
        ConceptService conceptService = Context.getConceptService();
        ConceptMapType sameAs = conceptService.getConceptMapTypeByName("same-as");
        ConceptSource snomed = conceptService.getConceptSourceByName("SNOMED CT");
        ConceptReferenceTerm term = new ConceptReferenceTerm(snomed, "258703001", null);
        conceptService.saveConceptReferenceTerm(term);
        Concept days = conceptService.getConcept(28);
        days.addConceptMapping(new ConceptMap(term, sameAs));
        conceptService.saveConcept(days);
    }

    @Test
    public void shouldDrugAndAssociatedDoses() throws Exception {
        Patient patient = Context.getPatientService().getAllPatients().get(2);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI + patient.getUuid());
        request.addHeader("content-type", "application/json");

        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        Object ward = responseObject.get("ward");

    }

}