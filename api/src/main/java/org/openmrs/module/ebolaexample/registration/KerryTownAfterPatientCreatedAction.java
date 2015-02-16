package org.openmrs.module.ebolaexample.registration;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaDemoData;
import org.openmrs.module.ebolaexample.metadata.KerryTownMetadata;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.registrationapp.action.AfterPatientCreatedAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class contains various thing we need to do hackily after creating a patient because the registrationapp module
 * doesn't support them yet:
 * <ul>
 *     <li>Assign a manually-entered Kerry Town ID</li>
 *     <li>Start a visit</li>
 *     <li>Admit to inpatient</li>
 * </ul>
 */
@Component("kerryTownAfterPatientCreatedAction")
public class KerryTownAfterPatientCreatedAction implements AfterPatientCreatedAction {

    public static final String KERRY_TOWN_ID = "kerryTownId";

    @Autowired
    private PatientService patientService;

    @Autowired
    private AdtService adtService;

    @Autowired
    private ProviderService providerService;

    @Override
    public void afterPatientCreated(Patient patient, Map<String, String[]> params) {
        // assign a Kerry Town ID, if specified
        if (params.get("kerryTownId") != null && params.get(KERRY_TOWN_ID).length > 0) {
            String kerryTownId = params.get("kerryTownId")[0].trim();
            if (StringUtils.isNotEmpty(kerryTownId)) {
                if (patient.getPatientIdentifier() != null) {
                    patient.getPatientIdentifier().setPreferred(false);
                }
                PatientIdentifierType identifierType = MetadataUtils.existing(PatientIdentifierType.class, KerryTownMetadata._PatientIdentifierType.KERRY_TOWN_IDENTIFIER);
                PatientIdentifier patientIdentifier = new PatientIdentifier(kerryTownId, identifierType, null);
                patientIdentifier.setPreferred(true);
                patient.addIdentifier(patientIdentifier);
                patientService.savePatient(patient);
            }
        }

        EncounterRole encounterRole = MetadataUtils.existing(EncounterRole.class, EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        Map<EncounterRole, Set<Provider>> providers = new HashMap<EncounterRole, Set<Provider>>();
        providers.put(encounterRole, Collections.singleton(currentProvider()));

        Location etc = MetadataUtils.existing(Location.class, EbolaDemoData._Location.EBOLA_TREATMENT_UNIT);
        Visit visit = adtService.ensureActiveVisit(patient, etc);

        Location wards = MetadataUtils.existing(Location.class, EbolaDemoData._Location.INPATIENT_WARDS);
        AdtAction adtAction = new AdtAction(visit, wards, providers, AdtAction.Type.ADMISSION);
        adtService.createAdtEncounterFor(adtAction);
    }

    private Provider currentProvider() {
        Person current = Context.getAuthenticatedUser().getPerson();
        Collection<Provider> providersByPerson = providerService.getProvidersByPerson(current);
        return providersByPerson.iterator().next();
    }

}
