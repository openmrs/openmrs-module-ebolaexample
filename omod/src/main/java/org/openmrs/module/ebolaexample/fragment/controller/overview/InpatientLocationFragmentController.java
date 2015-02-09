package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.Outcome;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.page.controller.ChangePatientDischargePageController;
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

import java.util.*;

public class InpatientLocationFragmentController {

    public void controller(@FragmentParam(value = "activeVisit", required = false) VisitDomainWrapper activeVisit,
                           FragmentModel model) {

        Location currentLocation = null;
        if (activeVisit != null) {
            currentLocation = activeVisit.getInpatientLocation(new Date());
        }
        Location currentWard = closestLocationWithTag(currentLocation,
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_SUSPECT_WARD),
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_CONFIRMED_WARD),
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_RECOVERY_WARD));

        if (currentWard == null) {
            currentWard = currentLocation;
        }

        Location currentBed = closestLocationWithTag(currentLocation,
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.INPATIENT_BED));

        model.addAttribute("currentWard", currentWard);
        model.addAttribute("currentBed", currentBed);
    }

    private Location closestLocationWithTag(Location location, LocationTag... tags) {
        if (location == null) {
            return null;
        }
        for (LocationTag tag : tags) {
            if (location.hasTag(tag.getName())) {
                return location;
            }
        }
        return closestLocationWithTag(location.getParentLocation(), tags);
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
                           @SpringBean AdtService adtService,
                           @SpringBean BedAssignmentService bedAssignmentService) {

        try {
            bedAssignmentService.assign(patient, location);
            return new SuccessResult("Transferred");
        } catch (APIException e) {
            return new FailureResult(e.getMessage());
        }
    }

}
