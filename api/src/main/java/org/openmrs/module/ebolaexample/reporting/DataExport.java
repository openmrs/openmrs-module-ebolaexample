package org.openmrs.module.ebolaexample.reporting;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.KerryTownMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.ObsValueConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.ProgramEnrollmentsForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

import java.util.Date;

public class DataExport {

    public DataSetDefinition buildRegistrationDataSetDefinition() {
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();

        addRegistrationDate(dsd);

        addGivenAndFamilyNames(dsd);

        addGender(dsd);

        addBirthdate(dsd);

        addAddress(dsd);

        addPhoneNumber(dsd);

        addNextOfKin(dsd);

        addIdNumber(dsd);

        EncounterType registrationEncounterType = MetadataUtils.existing(EncounterType.class, EbolaMetadata._EncounterType.EBOLA_REGISTRATION);

        addWeight(dsd, registrationEncounterType);

        addTypeOfPatient(dsd, registrationEncounterType);

        addEbolaStage(dsd, registrationEncounterType);

        return dsd;
    }

    public PatientDataSetDefinition buildDischargeDataSetDefinition() {
        PatientDataSetDefinition dsd = new PatientDataSetDefinition();

        ProgramEnrollmentsForPatientDataDefinition programEnrollmentsDefinition = new ProgramEnrollmentsForPatientDataDefinition();
        Program program = Context.getProgramWorkflowService().getProgram(1);

        programEnrollmentsDefinition.setProgram(program);
        programEnrollmentsDefinition.setWhichEnrollment(TimeQualifier.FIRST);
        programEnrollmentsDefinition.addParameter(new Parameter("datedischarged", "datedischarged", Date.class));
        programEnrollmentsDefinition.addParameter(new Parameter("outcome", "outcome", Object.class));

        dsd.addColumn("datedischarged", programEnrollmentsDefinition, "", new PropertyConverter(PatientProgram.class, "dateCompleted"));
        dsd.addColumn("outcome", programEnrollmentsDefinition, "", new PropertyConverter(PatientProgram.class, "outcome"));

        return dsd;
    }

    private void addEbolaStage(PatientDataSetDefinition dsd, EncounterType registrationEncounterType) {
        Concept ebolaStageConcept = MetadataUtils.existing(Concept.class, EbolaMetadata._Concept.EBOLA_STAGE);
        ObsForPersonDataDefinition obsForPersonDataDefinition3 = new ObsForPersonDataDefinition("ebolastage", TimeQualifier.FIRST, ebolaStageConcept, null, null);
        obsForPersonDataDefinition3.addEncounterType(registrationEncounterType);
        dsd.addColumn("ebolastage", obsForPersonDataDefinition3, "", new ObsValueConverter());
    }

    private void addTypeOfPatient(PatientDataSetDefinition dsd, EncounterType registrationEncounterType) {
        Concept typeOfPatientConcept = MetadataUtils.existing(Concept.class, EbolaMetadata._Concept.TYPE_OF_PATIENT);
        ObsForPersonDataDefinition obsForPersonDataDefinition2 = new ObsForPersonDataDefinition("typeofpatient", TimeQualifier.FIRST, typeOfPatientConcept, null, null);
        obsForPersonDataDefinition2.addEncounterType(registrationEncounterType);
        dsd.addColumn("typeofpatient", obsForPersonDataDefinition2, "", new ObsValueConverter());
    }

    private void addWeight(PatientDataSetDefinition dsd, EncounterType registrationEncounterType) {
        Concept weightConcept = MetadataUtils.existing(Concept.class, EbolaMetadata._Concept.WEIGHT_IN_KG);
        ObsForPersonDataDefinition obsForPersonDataDefinition = new ObsForPersonDataDefinition("weight", TimeQualifier.FIRST, weightConcept, null, null);
        obsForPersonDataDefinition.addEncounterType(registrationEncounterType);
        dsd.addColumn("weight", obsForPersonDataDefinition, "", new ObsValueConverter());
    }

    private void addNextOfKin(PatientDataSetDefinition dsd) {
        PersonAttributeType nextOfKinName = MetadataUtils.existing(PersonAttributeType.class, EbolaMetadata._PersonAttributeType.NEXT_OF_KIN_NAME);
        dsd.addColumn("nextofkinname", new PersonAttributeDataDefinition("nextofkinname", nextOfKinName), "", new PropertyConverter(PersonAttribute.class, "value"));

        PersonAttributeType nextOfKinPhoneNumber = MetadataUtils.existing(PersonAttributeType.class, EbolaMetadata._PersonAttributeType.NEXT_OF_KIN_PHONE);
        dsd.addColumn("nextofkinphonenumber", new PersonAttributeDataDefinition("nextofkinphonenumber", nextOfKinPhoneNumber), "", new PropertyConverter(PersonAttribute.class, "value"));
    }

    private void addPhoneNumber(PatientDataSetDefinition dsd) {
        PersonAttributeType phoneNumber = MetadataUtils.existing(PersonAttributeType.class, EbolaMetadata._PersonAttributeType.TELEPHONE_NUMBER);
        dsd.addColumn("phone", new PersonAttributeDataDefinition("phone", phoneNumber), "", new PropertyConverter(PersonAttribute.class, "value"));
    }

    private void addAddress(PatientDataSetDefinition dsd) {
        dsd.addColumn("district", new PreferredAddressDataDefinition(), "", new PropertyConverter(PersonAddress.class, "countyDistrict"));
        dsd.addColumn("chiefdom", new PreferredAddressDataDefinition(), "", new PropertyConverter(PersonAddress.class, "address2"));
        dsd.addColumn("cityvillage", new PreferredAddressDataDefinition(), "", new PropertyConverter(PersonAddress.class, "cityVillage"));
        dsd.addColumn("address", new PreferredAddressDataDefinition(), "", new PropertyConverter(PersonAddress.class, "address1"));
    }

    private void addBirthdate(PatientDataSetDefinition dsd) {
        dsd.addColumn("birthdate", new BirthdateDataDefinition(), "", new BirthdateConverter());
        dsd.addColumn("birthdateestimated", new BirthdateDataDefinition(), "", new PropertyConverter(Birthdate.class, "estimated"));
    }

    private void addGender(PatientDataSetDefinition dsd) {
        dsd.addColumn("gender", new GenderDataDefinition(), "");
    }

    private void addGivenAndFamilyNames(PatientDataSetDefinition dsd) {
        dsd.addColumn("givenname", new PreferredNameDataDefinition(), "", new PropertyConverter(PersonName.class, "givenName"));
        dsd.addColumn("familyname", new PreferredNameDataDefinition(), "", new PropertyConverter(PersonName.class, "familyName"));
    }

    private void addIdNumber(PatientDataSetDefinition dsd) {
        PatientIdentifierType kerryTownId = MetadataUtils.existing(PatientIdentifierType.class, KerryTownMetadata._PatientIdentifierType.KERRY_TOWN_IDENTIFIER);
        PatientIdentifierDataDefinition patientIdentifierDataDefinition = new PatientIdentifierDataDefinition("ktidnumber", kerryTownId);
        patientIdentifierDataDefinition.setIncludeFirstNonNullOnly(true);
        dsd.addColumn("ktidnumber", patientIdentifierDataDefinition, "", new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    private void addRegistrationDate(PatientDataSetDefinition dsd) {
        SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
        sqlPatientDataDefinition.setSql("SELECT patient_id, encounter_datetime FROM encounter WHERE patient_id IN (:patientIds)");
        dsd.addColumn("registrationdate", sqlPatientDataDefinition, "");
    }

    public ReportDefinition buildFullDataExport() {
        ReportDefinition reportDefinition = new ReportDefinition();
        reportDefinition.addDataSetDefinition("patientregistration", buildRegistrationDataSetDefinition(), null);
        reportDefinition.addDataSetDefinition("patientdischarge", buildDischargeDataSetDefinition(), null);

        return reportDefinition;
    }
}
