package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MostRecentEncounterFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @FragmentParam(value = "encounterType", required = false) EncounterType encounterType,
                           @SpringBean("encounterService") EncounterService encounterService,
                           FragmentModel model) {

        Collection<EncounterType> encounterTypes = encounterType == null ? null : Collections.singleton(encounterType);

        Encounter lastEncounter = null;
        List<Encounter> encounters = encounterService.getEncounters(patient.getPatient(), null, null, null, null, encounterTypes, null, null, null, false);
        if (encounters.size() > 0) {
            Collections.sort(encounters, new Comparator<Encounter>() {
                @Override
                public int compare(Encounter left, Encounter right) {
                    return right.getEncounterDatetime().compareTo(left.getEncounterDatetime());
                }
            });
            lastEncounter = encounters.get(0);
            // TODO: switch on lastEncounter.getForm() to decide how to display
        }
        model.addAttribute("lastEncounter", lastEncounter);
    }

}
