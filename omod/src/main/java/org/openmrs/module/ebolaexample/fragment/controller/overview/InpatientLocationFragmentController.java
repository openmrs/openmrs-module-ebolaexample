package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InpatientLocationFragmentController {

    public void controller(@FragmentParam("activeVisit") VisitDomainWrapper activeVisit,
                           @SpringBean AdtService adtService,
                           FragmentModel model) {
        Location currentLocation = null;
        if (activeVisit != null) {
            currentLocation = activeVisit.getInpatientLocation(new Date());
        }
        model.addAttribute("currentLocation", currentLocation);
    }

    public Object startOutpatientVisit(@RequestParam("patient") Patient patient,
                                       UiSessionContext uiSessionContext,
                                       @SpringBean AdtService adtService) {
        return adtService.ensureActiveVisit(patient, uiSessionContext.getSessionLocation());
    }
    
    public Object admit(@RequestParam("patient") Patient patient,
                        @RequestParam("location") Location location,
                        UiSessionContext uiSessionContext,
                        @SpringBean EmrApiProperties emrApiProperties,
                        @SpringBean AdtService adtService) {

        Map<EncounterRole, Set<Provider>> providers = new HashMap<EncounterRole, Set<Provider>>();
        providers.put(MetadataUtils.existing(EncounterRole.class, EbolaMetadata._EncounterRole.CLINICIAN),
                Collections.singleton(uiSessionContext.getCurrentProvider()));

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);
        AdtAction adtAction = new AdtAction(activeVisit.getVisit(), location, providers, AdtAction.Type.ADMISSION);

        Encounter created = adtService.createAdtEncounterFor(adtAction);
        return new SuccessResult("Admitted");
    }

    public Object transfer(@RequestParam("patient") Patient patient,
                           @RequestParam("location") Location location,
                           UiSessionContext uiSessionContext,
                           @SpringBean EmrApiProperties emrApiProperties,
                           @SpringBean AdtService adtService) {

        Map<EncounterRole, Set<Provider>> providers = new HashMap<EncounterRole, Set<Provider>>();
        providers.put(MetadataUtils.existing(EncounterRole.class, EbolaMetadata._EncounterRole.CLINICIAN),
                Collections.singleton(uiSessionContext.getCurrentProvider()));

        VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, location);
        if (!activeVisit.isAdmitted()) {
            return new FailureResult("Not admitted (so cannot transfer)");
        }
        AdtAction adtAction = new AdtAction(activeVisit.getVisit(), location, providers, AdtAction.Type.TRANSFER);

        Encounter created = adtService.createAdtEncounterFor(adtAction);
        return new SuccessResult("Transferred");
    }

}
