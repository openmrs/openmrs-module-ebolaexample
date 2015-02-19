package org.openmrs.module.ebolaexample.page.controller

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.openmrs.*
import org.openmrs.api.ConceptService
import org.openmrs.api.PatientService
import org.openmrs.api.ProgramWorkflowService
import org.openmrs.api.VisitService
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata
import org.openmrs.module.ebolaexample.metadata.EbolaTestData
import org.openmrs.module.ebolaexample.rest.WebMethods
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

import java.text.SimpleDateFormat

class ChangePatientDischargePageControllerTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private PatientService patientService

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    private WebMethods webMethods;

    @Autowired
    private ProgramWorkflowService programWorkflowService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private ChangePatientDischargePageController changePatientDischargePageController

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private String requestURI;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
    }

    @Ignore
    @Test
    public void shouldReturnModelAttributes() throws Exception {
        Patient patient = patientService.getPatientByUuid("da7f524f-27ce-4bb2-86d6-6d1d05312bd5");

        checkAndSetupVisitAndProgram(patient);

        Concept concept = conceptService.getConceptByUuid("95312123-e0c2-466d-b6b1-cb6e990d0d65");

        requestURI = "/ebolaexample/changePatientDischarge.page?patientUuid=" + patient.getUuid();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", requestURI);

        request.addParameter("patientUuid", patient.getUuid());
        request.addParameter("outCome", concept.getUuid());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        request.addParameter("dateCompleted", simpleDateFormat.format(new Date()));

        response = webMethods.handle(request);
    }

    private void checkAndSetupVisitAndProgram(Patient patient) {
        List<Visit> activeVisits = visitService.getActiveVisitsByPatient(patient);
        if (activeVisits.size() == 0) {
            VisitType visitType = visitService.getVisitTypeByUuid("759799ab-c9a5-435e-b671-77773ada74e4");
            Visit visit = new Visit(patient, visitType, new Date());
            visitService.saveVisit(visit);
        }

        Program ebolaProgram = programWorkflowService.getProgramByUuid(EbolaMetadata._Program.EBOLA_PROGRAM);

        List<PatientProgram> enrollments = programWorkflowService.getPatientPrograms(patient, ebolaProgram, null, null, null, null, false);
        if (enrollments.size() == 0) {
            PatientProgram pp = new PatientProgram();
            pp.setPatient(patient);
            pp.setProgram(ebolaProgram);
            pp.setDateEnrolled(new Date());
            programWorkflowService.savePatientProgram(pp);
        }
    }
}
