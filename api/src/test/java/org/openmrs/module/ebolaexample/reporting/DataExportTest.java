package org.openmrs.module.ebolaexample.reporting;

import org.junit.Test;
import org.openmrs.*;
import org.openmrs.contrib.testdata.TestDataManager;
import org.openmrs.module.ebolaexample.EbolaMetadataTest;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.KerryTownMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.DataSetRowList;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataExportTest extends EbolaMetadataTest {

    private static final String GIVEN_NAME = "Given";
    private static final String FAMILY_NAME = "Family";
    private static final String PHONE_NUMBER = "1234567890";
    private static final String NEXT_OF_KIN_NAME = "None";
    private static final String NEXT_OF_KIN_PHONE_NUMBER = "0987654321";
    private static final String KERRY_TOWN_ID = "KT-1-11111";
    private static final String WEIGHT = "70";
    private static final String EBOLA_STAGE = "Severe";
    private static final String TYPE_OF_PATIENT = "Confirmed";
    private static final String DISTRICT = "District";
    private static final String CHIEFDOM = "Chief";
    private static final String CITY_VILLAGE = "City";
    private static final String ADDRESS = "123 Fake Street";
    private static final Date NOW = new Date();

    private static final String GENDER = "M";

    @Autowired
    TestDataManager testDataManager;

    @Autowired
    EbolaMetadata ebolaMetadata;

    @Autowired
    KerryTownMetadata kerryTownMetadata;

    @Autowired
    DataSetDefinitionService dataSetDefinitionService;

    @Autowired
    PatientDataService patientDataService;
    DataExport dataExport = new DataExport();

    @Test
    public void testDischargeDataExport() throws Exception {
        ebolaMetadata.install();
        kerryTownMetadata.install();

        createTestPatient();

        PatientDataSetDefinition dischargeDataSetDefinition = dataExport.buildDischargeDataSetDefinition();
        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(dischargeDataSetDefinition, new EvaluationContext());
        DataSetRow row = dataSet.getRows().get(0);

        assertThat(row.getColumnValue("datedischarged").toString(), is(NOW.toString()));
    }

    @Test
    public void testRegistrationDataExport() throws Exception {
        ebolaMetadata.install();
        kerryTownMetadata.install();

        createTestPatient();

        DataSetDefinition registrationDataSetDefinition = dataExport.buildRegistrationDataSetDefinition();
        SimpleDataSet dataSet = (SimpleDataSet) dataSetDefinitionService.evaluate(registrationDataSetDefinition, new EvaluationContext());
        DataSetRow patientRow = findPatientRow(dataSet.getRows(), GIVEN_NAME, FAMILY_NAME);


        assertThat(convertTimeStampToDateString((Timestamp) patientRow.getColumnValue("registrationdate")), is(NOW.toString()));

        assertThat((String) patientRow.getColumnValue("givenname"), is(GIVEN_NAME));
        assertThat((String) patientRow.getColumnValue("familyname"), is(FAMILY_NAME));
        assertThat((String) patientRow.getColumnValue("gender"), is(GENDER));
        assertThat((String) patientRow.getColumnValue("birthdate"), is(DateUtil.formatDate(NOW, "yyyy-MM-dd")));
        assertThat((Boolean) patientRow.getColumnValue("birthdateestimated"), is(false));

        assertThat((String) patientRow.getColumnValue("district"), is(DISTRICT));
        assertThat((String) patientRow.getColumnValue("chiefdom"), is(CHIEFDOM));
        assertThat((String) patientRow.getColumnValue("cityvillage"), is(CITY_VILLAGE));
        assertThat((String) patientRow.getColumnValue("address"), is(ADDRESS));
        assertThat((String) patientRow.getColumnValue("phone"), is(PHONE_NUMBER));
        assertThat((String) patientRow.getColumnValue("nextofkinname"), is(NEXT_OF_KIN_NAME));
        assertThat((String) patientRow.getColumnValue("nextofkinphonenumber"), is(NEXT_OF_KIN_PHONE_NUMBER));

        assertThat((String) patientRow.getColumnValue("ktidnumber"), is(KERRY_TOWN_ID));

        assertThat((String) patientRow.getColumnValue("weight"), is(WEIGHT));
        assertThat((String) patientRow.getColumnValue("typeofpatient"), is(TYPE_OF_PATIENT));
        assertThat((String) patientRow.getColumnValue("ebolastage"), is(EBOLA_STAGE));
    }

    private void createTestPatient() {
        PersonAddress address = new PersonAddress();
        address.setCountyDistrict(DISTRICT);
        address.setAddress2(CHIEFDOM);
        address.setCityVillage(CITY_VILLAGE);
        address.setAddress1(ADDRESS);

        PersonAttributeType phoneNumberAttributeType = MetadataUtils.existing(
                PersonAttributeType.class, EbolaMetadata._PersonAttributeType.TELEPHONE_NUMBER);
        PersonAttributeType nextOfKinNameAttributeType = MetadataUtils.existing(
                PersonAttributeType.class, EbolaMetadata._PersonAttributeType.NEXT_OF_KIN_NAME);
        PersonAttributeType nextOfKinPhoneAttributeType = MetadataUtils.existing(
                PersonAttributeType.class, EbolaMetadata._PersonAttributeType.NEXT_OF_KIN_PHONE);

        PatientIdentifierType kerryTownId = MetadataUtils.existing(
                PatientIdentifierType.class, KerryTownMetadata._PatientIdentifierType.KERRY_TOWN_IDENTIFIER);

        Concept weightConcept = MetadataUtils.existing(Concept.class, EbolaMetadata._Concept.WEIGHT_IN_KG);
        Concept typeOfPatientConcept = MetadataUtils.existing(Concept.class, EbolaMetadata._Concept.TYPE_OF_PATIENT);
        Concept ebolaStageConcept = MetadataUtils.existing(Concept.class, EbolaMetadata._Concept.EBOLA_STAGE);

        Patient patient = testDataManager.patient()
                .name(GIVEN_NAME, FAMILY_NAME)
                .male()
                .birthdate(NOW)
                .birthdateEstimated(false)
                .address(address)
                .personAttribute(phoneNumberAttributeType, PHONE_NUMBER)
                .personAttribute(nextOfKinNameAttributeType, NEXT_OF_KIN_NAME)
                .personAttribute(nextOfKinPhoneAttributeType, NEXT_OF_KIN_PHONE_NUMBER)
                .identifier(kerryTownId, KERRY_TOWN_ID)
                .save();

        testDataManager.encounter()
                .patient(patient)
                .encounterType(EbolaMetadata._EncounterType.EBOLA_REGISTRATION)
                .encounterDatetime(NOW)
                .obs(weightConcept, WEIGHT)
                .obs(typeOfPatientConcept, TYPE_OF_PATIENT)
                .obs(ebolaStageConcept, EBOLA_STAGE)
                .save();

        testDataManager.patientProgram().program(new Program(1)).dateCompleted(NOW).patient(patient).save();
    }

    private String convertTimeStampToDateString(Timestamp timestamp) {
        long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
        return new java.util.Date(milliseconds).toString();
    }

    private DataSetRow findPatientRow(DataSetRowList rows, String givenName, String familyName) {
        for (DataSetRow row : rows) {
            if (row.getColumnValue("givenname").equals(givenName) && row.getColumnValue("familyname").equals(familyName)) {
                return row;
            }
        }
        return null;
    }
}