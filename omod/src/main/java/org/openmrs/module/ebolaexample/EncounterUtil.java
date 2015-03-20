package org.openmrs.module.ebolaexample;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.List;

public class EncounterUtil {

    public static Encounter lastEncounter(EncounterService service, Patient patient, EncounterType type, List<Form> enteredViaForms) {
        List<Encounter> encounters = service.getEncounters(patient, null, null, null, enteredViaForms, Arrays.asList(type), null, null, null, false);
        return mostRecent(encounters);
    }

    private static Encounter mostRecent(List<Encounter> encounters) {
        Encounter mostRecent = null;
        if (encounters != null) {
            for (Encounter candidate : encounters) {
                if (mostRecent == null || OpenmrsUtil.compare(mostRecent.getEncounterDatetime(), candidate.getEncounterDatetime()) < 0) {
                    mostRecent = candidate;
                }
            }
        }
        return mostRecent;
    }
}
