package org.openmrs.module.ebolaexample.rest;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class WardResourceTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    @Autowired
    AdtService adtService;

    @Autowired
    BedAssignmentService bedAssignmentService;

    @Autowired
    PatientService patientService;

    @Autowired
    ProviderService providerService;

    private WardResource resource;

    @Before
    public void setUp() throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        resource = new WardResource();
    }

    @Test
    public void testGetAll() throws Exception {
        RequestContext requestContext = new RequestContext();
        requestContext.setRepresentation(Representation.REF);
        SimpleObject all = resource.getAll(requestContext);

        List<SimpleObject> results = (List<SimpleObject>) all.get("results");

        // just test one to make sure it's right
        SimpleObject ward = findWithUuid(results, EbolaTestData._Location.SUSPECT_WARD);
        assertNotNull(ward);
        assertThat((String) ward.get("type"), is("suspect"));
        assertThat((String) ward.get("display"), is("Suspect Ward"));
    }

    @Test
    public void testGetOne() throws Exception {
        Location ward = MetadataUtils.existing(Location.class, EbolaTestData._Location.SUSPECT_WARD);
        Location bed = MetadataUtils.existing(Location.class, EbolaTestData._Location.SUSPECT_BED_1);

        Patient patient = patientService.getPatient(7);
        admit(patient, ward);
        bedAssignmentService.assign(patient, bed);

        RequestContext requestContext = new RequestContext();
        SimpleObject result = (SimpleObject) resource.retrieve(EbolaTestData._Location.SUSPECT_WARD, requestContext);

        assertThat((String) result.get("uuid"), is(EbolaTestData._Location.SUSPECT_WARD));
        assertThat((String) result.get("type"), is("suspect"));
        List<SimpleObject> bedAssignments = (List<SimpleObject>) result.get("bedAssignments");
        assertThat(bedAssignments.size(), is(1));
        assertThat((String) getProperty(bedAssignments.get(0), "patient.uuid"), is(patient.getUuid()));
        assertThat((String) getProperty(bedAssignments.get(0), "bed.uuid"), is(bed.getUuid()));
    }

    private void admit(Patient patient, Location ward) {
        Visit visit = adtService.ensureActiveVisit(patient, ward);
        HashMap<EncounterRole, Set<Provider>> providers = new HashMap<EncounterRole, Set<Provider>>();
        providers.put(MetadataUtils.existing(EncounterRole.class, EbolaMetadata._EncounterRole.CLINICIAN),
                Collections.singleton(providerService.getProviderByIdentifier(EbolaTestData.DOCTOR_PROVIDER_IDENTIFIER)));
        adtService.createAdtEncounterFor(new AdtAction(visit, ward, providers, AdtAction.Type.ADMISSION));
    }

    private SimpleObject findWithUuid(List<SimpleObject> results, String uuid) {
        for (SimpleObject candidate : results) {
            if (uuid.equals(candidate.get("uuid"))) {
                return candidate;
            }
        }
        return null;
    }

}