package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterRole;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationTag;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;

@Component("ebolaMetadata")
public class EbolaMetadata extends AbstractMetadataBundle {

    public static class _Concept {
        public static final String EBOLA_PROGRAM = "162637AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"; // from CIEL
    }

    public static class _Program {
        public static final String EBOLA_PROGRAM = "ed5589a8-5761-11e4-af12-660e112eb3f5";
    }

    public static class _EncounterType {
        public static final String EBOLA_CASE_INVESTIGATION = "4b408f26-5763-11e4-af12-660e112eb3f5";
        public static final String EBOLA_INPATIENT_FOLLOWUP = "83413734-587d-11e4-af12-660e112eb3f5";
    }

    public static class _EncounterRole {
        public static final String CLINICIAN = "756b4288-584d-11e4-af12-660e112eb3f5";
    }

    public static class _LocationTag {
        public static final String EBOLA_HOT_ZONE_AREA = "18789832-582a-11e4-af12-660e112eb3f5";
        public static final String EBOLA_OBSERVATION_AREA = "2e78a6e0-582a-11e4-af12-660e112eb3f5";
        public static final String VISIT_LOCATION = "a971992a-5838-11e4-af12-660e112eb3f5";
    }

    public static class _Form {
        public static final String EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS = "c1d1b5b7-2d51-4f58-b8f3-7d9cb542fe4a";
        public static final String INPATIENT_OBSERVATIONS_AND_TREATMENT = "ab215dd2-59ff-11e4-af12-660e112eb3f5";
    }

    @Override
    public void install() throws Exception {
        install(program("Ebola", "Treatment of Ebola patients and observation of suspects", _Concept.EBOLA_PROGRAM, _Program.EBOLA_PROGRAM));

        install(locationTag("Ebola Hot Zone Area", "Area where patients known to have Ebola Virus Disease are quarantined and isolated", _LocationTag.EBOLA_HOT_ZONE_AREA));
        install(locationTag("Ebola Observation Area", "Area where patients suspected to possibly have Ebola Virus Disease are quarantined while awaiting test results", _LocationTag.EBOLA_OBSERVATION_AREA));
        install(locationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS, "Top-level location that a visit is attached to", _LocationTag.VISIT_LOCATION));

        install(encounterRole("Clinician", "Clinician", _EncounterRole.CLINICIAN));

        install(encounterType("Ebola Inpatient Followup", "Clinical checkup on a hospitalized Ebola patient or suspect", _EncounterType.EBOLA_INPATIENT_FOLLOWUP));
        install(encounterType("Ebola Case Investigation", "To Do", _EncounterType.EBOLA_CASE_INVESTIGATION));

        install(form("Ebola Inpatient Observations and Treatment", "", _EncounterType.EBOLA_INPATIENT_FOLLOWUP, "0.1", _Form.INPATIENT_OBSERVATIONS_AND_TREATMENT));
        install(form("Ebola clinical signs and symptoms", "", _EncounterType.EBOLA_CASE_INVESTIGATION, "0.1", _Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS));
    }

}
