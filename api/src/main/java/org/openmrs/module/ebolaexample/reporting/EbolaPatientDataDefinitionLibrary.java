package org.openmrs.module.ebolaexample.reporting;

import org.openmrs.Encounter;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EbolaPatientDataDefinitionLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

    public static final String PREFIX = "ebolaexample.patientdata.";

    public static final String INPATIENT_LOCATION = "inpatientLocation";

    @Autowired
    EmrApiProperties emrApiProperties;

    @Override
    public Class<? super PatientDataDefinition> getDefinitionType() {
        return PatientDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition(INPATIENT_LOCATION)
    public PatientDataDefinition getAssignedLocation() {
        return getAdmissionOrTransferEncounter(new PropertyConverter(Encounter.class, "location"));
    }

    private PatientDataDefinition getAdmissionOrTransferEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition adtEncounters = new EncountersForPatientDataDefinition();
        adtEncounters.setTypes(Arrays.asList(emrApiProperties.getAdmissionEncounterType(), emrApiProperties.getTransferWithinHospitalEncounterType()));
        adtEncounters.setOnlyInActiveVisit(true);
        adtEncounters.setWhich(TimeQualifier.LAST);
        return new ConvertedPatientDataDefinition(adtEncounters,converters);
    }
}
