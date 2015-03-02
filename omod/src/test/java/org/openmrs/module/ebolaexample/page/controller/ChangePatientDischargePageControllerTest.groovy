package org.openmrs.module.ebolaexample.page.controller

import org.junit.Assert;
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.openmrs.*
import org.openmrs.api.ConceptService
import org.openmrs.api.PatientService
import org.openmrs.api.ProgramWorkflowService
import org.openmrs.api.VisitService
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata
import org.openmrs.module.ebolaexample.metadata.EbolaTestData
import org.openmrs.module.ebolaexample.rest.WebMethods
import org.openmrs.ui.framework.UiUtils
import org.openmrs.ui.framework.page.PageModel
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

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

    @Mock
    private UiUtils ui;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private String requestURI;
    private String outComeUuid = "95312123-e0c2-466d-b6b1-cb6e990d0d65";
    private String patientUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
        Patient patient = patientService.getPatientByUuid(patientUuid);
        checkAndSetupVisitAndProgram(patient);
    }

    @Test
    public void shouldReturnModelAttributes() throws Exception {

        def pageModel = new PageModel();

        changePatientDischargePageController.post(patientService,
                programWorkflowService,
                conceptService,
                visitService,
                ui,
                patientUuid,
                outComeUuid,
                new Date(),
                pageModel);

        Assert.assertEquals("Discharge information modified successfully", pageModel.getAttribute("success").toString());
    }


    @Test
    public void shouldSetPatientAsDeathWhenTheOutcomeIsDiedOnWard() throws Exception {

        VerifyOutCome(ChangePatientDischargePageController.OutComeType.DiedOnWard)
    }

    @Test
    public void shouldSetPatientAsDeathWhenTheOutcomeIsDiedOnArrival() throws Exception {

        VerifyOutCome(ChangePatientDischargePageController.OutComeType.DeadOnArrival)
    }

    private void VerifyOutCome(ChangePatientDischargePageController.OutComeType outComeType) {
        def outcomeUuid = String.format("%sAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", outComeType.toString());

        def outcome = new Concept(Integer.valueOf(outComeType.toString()))
        outcome.addName(new ConceptName("test", Locale.ENGLISH));
        outcome.setUuid(outcomeUuid)
        conceptService.saveConcept(outcome);


        def completedDate = new Date()
        changePatientDischargePageController.post(patientService,
                programWorkflowService,
                conceptService,
                visitService,
                ui,
                patientUuid,
                outcomeUuid,
                completedDate,
                new PageModel());
        def patient = patientService.getPatientByUuid(patientUuid)
        Assert.assertTrue(patient.getDead())
        Assert.assertEquals(conceptService.getConceptByUuid(outcomeUuid), patient.getCauseOfDeath())
        Assert.assertEquals(completedDate.toString(), patient.getDeathDate().toString())
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
            pp.setDateEnrolled(new Date().minus(1));
            programWorkflowService.savePatientProgram(pp);
        }
    }
}
