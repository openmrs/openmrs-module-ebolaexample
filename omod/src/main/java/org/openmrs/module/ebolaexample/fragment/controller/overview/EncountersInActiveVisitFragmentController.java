package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Iterator;
import java.util.List;

public class EncountersInActiveVisitFragmentController {

    public void controller(@FragmentParam("patient") PatientDomainWrapper patient,
                           @FragmentParam(value = "activeVisit", required = false) VisitDomainWrapper activeVisit,
                           @FragmentParam(value = "encounterType", required = false) EncounterType encounterType,
                           FragmentModel model) {
        List<Encounter> encounters = null;
        if (activeVisit != null) {
            encounters = activeVisit.getSortedEncounters();
            if (encounterType != null) {
                for (Iterator<Encounter> i = encounters.iterator(); i.hasNext(); ) {
                    if (!encounterType.equals(i.next().getEncounterType())) {
                        i.remove();
                    }
                }
            }
        }
        model.addAttribute("encounters", encounters);
    }

}
