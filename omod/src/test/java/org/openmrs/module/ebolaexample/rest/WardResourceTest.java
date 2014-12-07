package org.openmrs.module.ebolaexample.rest;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

public class WardResourceTest extends BaseEbolaResourceTest {

    @Autowired
    BedAssignmentService bedAssignmentService;

    @Autowired
    PatientService patientService;

    @Test
    public void testGetAll() throws Exception {
        SimpleObject all = toSimpleObject(handle(request(GET, "ward")));
        List<Map> results = (List<Map>) all.get("results");

        // just test one to make sure it's right
        Map ward = findWithUuid(results, EbolaTestData._Location.SUSPECT_WARD);
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

        SimpleObject result = toSimpleObject(handle(request(GET, "ward/" + EbolaTestData._Location.SUSPECT_WARD)));

        assertThat((String) result.get("uuid"), is(EbolaTestData._Location.SUSPECT_WARD));
        assertThat((String) result.get("type"), is("suspect"));
        List<SimpleObject> bedAssignments = (List<SimpleObject>) result.get("bedAssignments");
        assertThat(bedAssignments.size(), is(1));
        assertThat((String) getProperty(bedAssignments.get(0), "patient.uuid"), is(patient.getUuid()));
        assertThat((String) getProperty(bedAssignments.get(0), "bed.uuid"), is(bed.getUuid()));
    }

}