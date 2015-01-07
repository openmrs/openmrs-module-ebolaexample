package org.openmrs.module.ebolaexample.metadata;

import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KerryTownMetadataTest extends BaseModuleContextSensitiveTest {

    @Autowired
    KerryTownMetadata kerryTownMetadata;

    @Autowired
    PatientService patientService;

    @Test
    public void testInstall() throws Exception {
        kerryTownMetadata.install();

        PatientIdentifierType kerryTownId = patientService.getPatientIdentifierTypeByUuid(KerryTownMetadata._PatientIdentifierType.KERRY_TOWN_IDENTIFIER);
        assertThat(kerryTownId.getName(), is("Kerry Town ID"));
    }

}