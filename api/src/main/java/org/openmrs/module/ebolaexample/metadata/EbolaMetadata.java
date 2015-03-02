package org.openmrs.module.ebolaexample.metadata;

import org.apache.commons.lang.StringUtils;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.customdatatype.datatype.LocationDatatype;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Component;

import java.util.HashSet;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.conceptNameTag;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterRole;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.encounterType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.form;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.locationTag;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.personAttributeType;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.program;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.visitAttributeType;

@Component("ebolaMetadata")
public class EbolaMetadata extends AbstractMetadataBundle {

    public static class _Concept { // all of these are from CIEL and we don't create them here
        public static final String EBOLA_PROGRAM = "162637AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String WEIGHT_IN_KG = "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String EBOLA_STAGE = "162834AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        public static final String TYPE_OF_PATIENT = "162828AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    }

    public static class _ConceptNameTag {
        public static final String PREFERRED = "cae27b08-9cf6-11e4-b773-e0fe58bba1f4";
    }

    public static class _Program {
        public static final String EBOLA_PROGRAM = "ed5589a8-5761-11e4-af12-660e112eb3f5";
    }

    public static class _EncounterType {
        public static final String EBOLA_REGISTRATION = "98963e52-863d-11e4-9fc5-eede903351fb";
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

    public static class _Role {
        public static final String WARD_ROUNDING_TEAM = "Ward Rounding Team";
    }

    public static class _PersonAttributeType {
        public static final String TELEPHONE_NUMBER = "14d4f066-15f5-102d-96e4-000c29c2a5d7";
        public static final String NEXT_OF_KIN_NAME = "a7255e8e-9d22-11e4-b773-e0fe58bba1f4";
        public static final String NEXT_OF_KIN_PHONE = "b0022b40-9d22-11e4-b773-e0fe58bba1f4";
    }

    public static class _OrderType {
        public static final String IV_FLUID_ORDER_TYPE_UUID = "95c340f8-fff2-4384-a010-be7ca609d442";
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

    private OrderType orderType(String name, String description, String uuid) {
        OrderType orderType = new OrderType(name, description, IvFluidOrder.class.getName());
        orderType.setUuid(uuid);
        return orderType;
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

        install(encounterType("Ebola Registration", "Creating a new patient record", _EncounterType.EBOLA_REGISTRATION));
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

        install(role(_Role.WARD_ROUNDING_TEAM, "Role for all users who are actually ward rounding teams", new HashSet<String>(), new HashSet<String>()));

        install(conceptNameTag("Ebola Preferred", "Preferred name for use in the Ebola ETC EMR", _ConceptNameTag.PREFERRED));

        // this is installed somewhere else, though I don't know where: install(personAttributeType("Telephone Number", "The telephone number for the person", String.class, null, false, 7, _PersonAttributeType.TELEPHONE_NUMBER));
        install(personAttributeType("Next of Kin Name", "Name of the patient's Next of Kin", String.class, null, false, 8, _PersonAttributeType.NEXT_OF_KIN_NAME));
        install(personAttributeType("Next of Kin Phone", "Phone number of the patient's Next of Kin", String.class, null, false, 9, _PersonAttributeType.NEXT_OF_KIN_PHONE));

        AdministrationService administrationService = Context.getAdministrationService();
        maybeSetGP(administrationService, OpenmrsConstants.GP_DRUG_ROUTES_CONCEPT_UUID, "162394AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        maybeSetGP(administrationService, OpenmrsConstants.GP_DRUG_DOSING_UNITS_CONCEPT_UUID, "162384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        maybeSetGP(administrationService, OpenmrsConstants.GP_DRUG_DISPENSING_UNITS_CONCEPT_UUID, "162402AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        maybeSetGP(administrationService, OpenmrsConstants.GP_DURATION_UNITS_CONCEPT_UUID, "1732AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        ensureOrderFrequencies(Context.getOrderService(), Context.getConceptService(), "160855AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        install(orderType("IV Fluid Order", "Order type for IV fluid orders", _OrderType.IV_FLUID_ORDER_TYPE_UUID));
    }

}
