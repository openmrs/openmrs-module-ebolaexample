package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class InpatientFollowupsFragmentController {

    public void get(@FragmentParam("patient") PatientDomainWrapper patient,
                    @FragmentParam(value = "activeVisit", required = false) VisitDomainWrapper visit,
                    UiSessionContext sessionContext,
                    FragmentModel model) {

        Location assignedLocation = visit == null ? null : visit.getInpatientLocation(new Date());

        model.addAttribute("patient", patient);
        model.addAttribute("visit", visit);
        model.addAttribute("location", assignedLocation);
        model.addAttribute("provider", sessionContext.getCurrentProvider());

        List<Encounter> encounters = null;
        if (visit != null) {
            EncounterType encounterType = MetadataUtils.existing(EncounterType.class, EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);
            encounters = visit.getSortedEncounters();
            for (Iterator<Encounter> i = encounters.iterator(); i.hasNext(); ) {
                if (!encounterType.equals(i.next().getEncounterType())) {
                    i.remove();
                }
            }
        }
        model.addAttribute("encounters", encounters);
    }

}
