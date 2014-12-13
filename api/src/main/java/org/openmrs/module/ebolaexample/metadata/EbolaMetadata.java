package org.openmrs.module.ebolaexample.metadata;

import org.apache.commons.lang.StringUtils;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.customdatatype.datatype.LocationDatatype;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterRole;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationTag;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.visitAttributeType;

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
        public static final String EBOLA_TREATMENT_ADMISSION = "8a08cc50-0139-4679-bc19-19dceec3b8ca";
        public static final String EBOLA_TRIAGE = "4a8da825-2896-4f3f-be07-2c3d9214c040";
        public static final String EBOLA_ASSESSMENT = "c49903a6-af3f-44ec-8ed3-abcfbfcea6e7";
        public static final String EBOLA_DISCHARGE = "181820aa-88c9-479b-9077-af92f5364329";
    }

    public static class _EncounterRole {
        public static final String CLINICIAN = "756b4288-584d-11e4-af12-660e112eb3f5";
    }

    public static class _LocationTag {
        public static final String EBOLA_SUSPECT_WARD = "2e78a6e0-582a-11e4-af12-660e112eb3f5";
        public static final String EBOLA_CONFIRMED_WARD = "18789832-582a-11e4-af12-660e112eb3f5";
        public static final String EBOLA_RECOVERY_WARD = "16250bf4-5e71-11e4-9305-df58197607bd";

        public static final String INPATIENT_BED = "c8bb459c-5e7d-11e4-9305-df58197607bd";

        public static final String VISIT_LOCATION = "a971992a-5838-11e4-af12-660e112eb3f5";
    }

    public static class _Form {
        public static final String EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS = "c1d1b5b7-2d51-4f58-b8f3-7d9cb542fe4a";
        public static final String INPATIENT_OBSERVATIONS_AND_TREATMENT = "ab215dd2-59ff-11e4-af12-660e112eb3f5";
        public static final String EBOLA_TRIAGE_FORM = "787608c8-512d-44fe-b793-fe54b660987a";
        public static final String EBOLA_ASSESSMENT_FORM = "230af74f-f1b5-4e43-ae6a-27208ed46540";
        public static final String EBOLA_DISCHARGE_FORM = "2541a157-fd45-41e0-ad2b-f6a24c65463a";
    }

	public static class _VisitAttributeType {
		public static final String ASSIGNED_WARD = "c7d117f0-6ff4-11e4-9803-0800200c9a66";
		public static final String ASSIGNED_BED = "c7d117f1-6ff4-11e4-9803-0800200c9a66";
	}

    private void maybeSetGP(AdministrationService service, String prop, String val) {
        GlobalProperty gp = service.getGlobalPropertyObject(prop);
        if (gp == null) {
            service.saveGlobalProperty(new GlobalProperty(prop, val));
        } else if (StringUtils.isEmpty(gp.getPropertyValue())) {
            gp.setPropertyValue(val);
            service.saveGlobalProperty(gp);
        }
    }

    private void ensureOrderFrequencies(OrderService orderService, ConceptService conceptService, String uuid) {
        if (orderService.getOrderFrequencies(true).size() == 0) {
            Concept set = conceptService.getConceptByUuid(uuid);
            if (set != null) {
                for (ConceptAnswer conceptAnswer : set.getAnswers()) {
                    Concept concept = conceptAnswer.getAnswerConcept();
                    if (concept != null) {
                        OrderFrequency frequency = new OrderFrequency();
                        frequency.setConcept(concept);
                        orderService.saveOrderFrequency(frequency);
                    }
                }
            }
        }
    }

    @Override
    public void install() throws Exception {
        install(program("Ebola", "Treatment of Ebola patients and observation of suspects", _Concept.EBOLA_PROGRAM, _Program.EBOLA_PROGRAM));

        install(locationTag("Ebola Confirmed Ward", "Area where patients known to have Ebola Virus Disease are quarantined and isolated", _LocationTag.EBOLA_CONFIRMED_WARD));
        install(locationTag("Ebola Suspect Ward", "Area where patients suspected to possibly have Ebola Virus Disease are quarantined while awaiting test results", _LocationTag.EBOLA_SUSPECT_WARD));
        install(locationTag("Ebola Recovery Ward", "Area where patients who have cleared their Ebola Virus infection stay before being discharged", _LocationTag.EBOLA_RECOVERY_WARD));
        install(locationTag("Inpatient Bed", "Bed in an inpatient ward where a patient may be assigned", _LocationTag.INPATIENT_BED));
        install(locationTag(EmrApiConstants.LOCATION_TAG_SUPPORTS_VISITS, "Top-level location that a visit is attached to", _LocationTag.VISIT_LOCATION));

        install(encounterRole("Clinician", "Clinician", _EncounterRole.CLINICIAN));

        install(encounterType("Ebola Inpatient Followup", "Clinical checkup on a hospitalized Ebola patient or suspect", _EncounterType.EBOLA_INPATIENT_FOLLOWUP));
        install(encounterType("Ebola Case Investigation", "To Do", _EncounterType.EBOLA_CASE_INVESTIGATION));
        install(encounterType("ETU Admission", "Admission to the Ebola Treatment Unit (ETU)",  _EncounterType.EBOLA_TREATMENT_ADMISSION));
        install(encounterType("ETU Triage", "Triage of patients arriving at Ebola Treatment Unit (ETU)",  _EncounterType.EBOLA_TRIAGE));
        install(encounterType("ETU Assessment", "Assessment of patients at Ebola Treatment Unit (ETU)",  _EncounterType.EBOLA_ASSESSMENT));
        install(encounterType("ETU Discharge", "Discharge patients at Ebola Treatment Unit (ETU)",  _EncounterType.EBOLA_DISCHARGE));

        install(form("Ebola Inpatient Observations and Treatment", "", _EncounterType.EBOLA_INPATIENT_FOLLOWUP, "0.1", _Form.INPATIENT_OBSERVATIONS_AND_TREATMENT));
        install(form("Ebola Clinical Signs and Symptoms", "", _EncounterType.EBOLA_CASE_INVESTIGATION, "0.1", _Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS));
        install(form("Ebola Triage", "", _EncounterType.EBOLA_TRIAGE, "2.0", _Form.EBOLA_TRIAGE_FORM));
        install(form("Ebola Assessment", "", _EncounterType.EBOLA_ASSESSMENT, "2.0", _Form.EBOLA_ASSESSMENT_FORM));
        install(form("Ebola Assessment", "", _EncounterType.EBOLA_DISCHARGE, "2.0", _Form.EBOLA_DISCHARGE_FORM));
     		
		install(visitAttributeType("Assigned ward", "", LocationDatatype.class, null, 0, 1, _VisitAttributeType.ASSIGNED_WARD));
		install(visitAttributeType("Assigned bed", "", LocationDatatype.class, null, 0, 1, _VisitAttributeType.ASSIGNED_BED));

        AdministrationService administrationService = Context.getAdministrationService();
        maybeSetGP(administrationService, OpenmrsConstants.GP_DRUG_ROUTES_CONCEPT_UUID, "162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        maybeSetGP(administrationService, OpenmrsConstants.GP_DRUG_DOSING_UNITS_CONCEPT_UUID, "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        maybeSetGP(administrationService, OpenmrsConstants.GP_DRUG_DISPENSING_UNITS_CONCEPT_UUID, "162402AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        maybeSetGP(administrationService, OpenmrsConstants.GP_DURATION_UNITS_CONCEPT_UUID, "1732AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        ensureOrderFrequencies(Context.getOrderService(), Context.getConceptService(), "160855AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

}
