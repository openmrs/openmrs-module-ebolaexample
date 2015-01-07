package org.openmrs.module.ebolaexample;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.api.WardAndBed;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BedAssignmentServiceTest extends BaseModuleContextSensitiveTest {

	protected static final String BASE_TEST_DATASET_XML = "baseTestDataset.xml";

	protected static final String INITIAL_DATA_XML = "BedAssignmentServiceTest-initialData.xml";

	@Autowired
	private LocationService locationService;

	@Autowired
	private BedAssignmentService bedAssignmentService;

	@Autowired
	private AdtService adtService;

	@Autowired
	private EbolaMetadata ebolaMetadata;

	@Before
	public void initialize() throws Exception {
		executeDataSet(BASE_TEST_DATASET_XML);
		ebolaMetadata.install();
		executeDataSet(INITIAL_DATA_XML);
		LocationTag inpatientBedTag = locationService.getLocationTagByUuid(EbolaMetadata._LocationTag.INPATIENT_BED);
        locationService.getLocation(5).addTag(MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.VISIT_LOCATION));
		locationService.getLocation(6).getTags().add(inpatientBedTag);
	}

	@Test
	@Verifies(value = "should assign patient to the bed location", method = "assign(Patient, Location)")
	public void assign_shouldAssignPatientToTheBedLocation() throws Exception {
		Patient patient = Context.getPatientService().getPatient(7);
		Location bed = Context.getLocationService().getLocation(6);
		Assert.assertFalse(adtService.getLastEncounter(patient) != null &&
				adtService.getLastEncounter(patient).getLocation().equals(bed));

		bedAssignmentService.assign(patient, bed);
		Encounter encounter = adtService.getLastEncounter(patient);
		Assert.assertTrue(encounter.getLocation().equals(bed));
		Assert.assertTrue(encounter.getVisit().getAttributes().size() == 2);
	}

	@Test
	@Verifies(value = "should get bed assignments for the ward location", method = "getBedAssignments(Location)")
	public void getBedAssignments_shouldGetBedAssignmentsForTheWardLocation() throws Exception {
		Patient patient = Context.getPatientService().getPatient(7);
		Location ward = Context.getLocationService().getLocation(5);
		Location bed = Context.getLocationService().getLocation(6);
		bedAssignmentService.assign(patient, bed);

		WardBedAssignments assignments = bedAssignmentService.getBedAssignments(ward);

		Assert.assertTrue(assignments.getBedAssignments().size() == 1);
		Assert.assertTrue(assignments.getBedAssignments().get(bed) != null);
	}

    @Test
    public void getPatientAssignedTo_shouldGetPatientAssignedToABed() throws Exception {
        Patient patient = Context.getPatientService().getPatient(7);
        Location bed = Context.getLocationService().getLocation(6);
        bedAssignmentService.assign(patient, bed);

        Patient assigned = bedAssignmentService.getPatientAssignedTo(bed);
        assertThat(assigned, is(patient));
    }

    @Test
    public void testGetBedAssignments() throws Exception {
        Patient patient = Context.getPatientService().getPatient(7);
        Location bed = Context.getLocationService().getLocation(6);
        Location ward = Context.getLocationService().getLocation(5);
        bedAssignmentService.assign(patient, bed);

        WardBedAssignments assignment = bedAssignmentService.getBedAssignments(ward);
        assertThat(assignment.getBedAssignments().size(), is(1));
        assertThat(assignment.getBedAssignments().get(bed), is(patient));
    }

    @Test
    public void testGetAllBedAssignments() throws Exception {
        Patient patient = Context.getPatientService().getPatient(7);
        Location bed = Context.getLocationService().getLocation(6);
        Location ward = Context.getLocationService().getLocation(5);
        bedAssignmentService.assign(patient, bed);

        List<WardBedAssignments> assignments = bedAssignmentService.getAllBedAssignments();
        for (WardBedAssignments assignment : assignments) {
            if (assignment.getWard().equals(ward)) {
                assertThat(assignment.getBedAssignments().size(), is(1));
                assertThat(assignment.getBedAssignments().get(bed), is(patient));
            }
            else {
                assertThat(assignment.getBedAssignments().size(), is(0));
            }
        }
    }

    @Test
    public void testGetAssignedWardAndBed() throws Exception {
        Patient patient = Context.getPatientService().getPatient(7);
        Location bed = Context.getLocationService().getLocation(6);
        Location ward = Context.getLocationService().getLocation(5);
        bedAssignmentService.assign(patient, bed);

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, ward);
        WardAndBed wardAndBed = bedAssignmentService.getAssignedWardAndBedFor(activeVisit.getVisit());

        assertThat(wardAndBed.getWard(), is(ward));
        assertThat(wardAndBed.getBed(), is(bed));
    }

}
