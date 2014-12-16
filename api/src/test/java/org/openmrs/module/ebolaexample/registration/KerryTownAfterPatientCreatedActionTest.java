package org.openmrs.module.ebolaexample.registration;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.ebolaexample.EbolaMetadataTest;
import org.openmrs.module.ebolaexample.metadata.EbolaDemoData;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.KerryTownMetadata;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class KerryTownAfterPatientCreatedActionTest extends EbolaMetadataTest {

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private KerryTownMetadata kerryTownMetadata;

    @Autowired
    private EbolaDemoData ebolaDemoData;

    @Autowired
    private KerryTownAfterPatientCreatedAction action;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AdtService adtService;

    @Autowired
    private LocationService locationService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ebolaMetadata.install();
        kerryTownMetadata.install();
        ebolaDemoData.install();
    }

    @Test
    public void testAfterPatientCreated() throws Exception {
        Patient created = new Patient();
        created.addName(new PersonName("A", "Patient", "Created"));
        created.addIdentifier(new PatientIdentifier("12345", patientService.getPatientIdentifierType(2), locationService.getLocation(1)));
        created.setGender("F");
        created.setBirthdateFromAge(30, new Date());
        patientService.savePatient(created);

        HashMap<String, String[]> params = new HashMap<String, String[]>();
        params.put(KerryTownAfterPatientCreatedAction.KERRY_TOWN_ID, new String[] { "KT-2-00006" });
        action.afterPatientCreated(created, params);

        PatientIdentifierType identifierType = MetadataUtils.existing(PatientIdentifierType.class, KerryTownMetadata._PatientIdentifierType.KERRY_TOWN_IDENTIFIER);
        Location etc = MetadataUtils.existing(Location.class, EbolaDemoData._Location.EBOLA_TREATMENT_UNIT);

        assertThat(created.getPatientIdentifier(identifierType).getIdentifier(), is("KT-2-00006"));

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(created, etc);
        assertNotNull(activeVisit);
        assertTrue(activeVisit.isAdmitted());
    }

}