package org.openmrs.module.ebolaexample.rest.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.ebolaexample.rest.BaseEbolaResourceTest;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PatientAssignmentControllerTest extends BaseEbolaResourceTest {

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    @Autowired
    BedAssignmentService bedAssignmentService;

    @Autowired
    PatientService patientService;

    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        response = new MockHttpServletResponse();
        requestURI = "/rest/" + RestConstants.VERSION_1 + "/ebola/assignment/";
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
    public void shouldGetPatientWardAndBed() throws Exception {
        Patient patient = Context.getPatientService().getAllPatients().get(2);

        Location ward = MetadataUtils.existing(Location.class, EbolaTestData._Location.SUSPECT_WARD);
        Location bed = MetadataUtils.existing(Location.class, EbolaTestData._Location.SUSPECT_BED_1);

        admit(patient, ward);
        bedAssignmentService.assign(patient, bed);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addParameter("patientUuid", patient.getUuid());
        request.addHeader("content-type", "application/json");

        response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        assertThat((String) responseObject.get("ward"), is("Suspect Ward"));
        assertThat((String) responseObject.get("bed"), is("Bed #1"));
    }

}