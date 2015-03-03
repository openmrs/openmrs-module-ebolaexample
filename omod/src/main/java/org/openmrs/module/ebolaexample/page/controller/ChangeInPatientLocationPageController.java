package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

public class ChangeInPatientLocationPageController {

    public void controller(@SpringBean("patientService") PatientService patientService,
                           @RequestParam(value = "patientUuid", required = false) String patientUuid,
                           @SpringBean AdtService adtService,
                           @SpringBean BedAssignmentService bedAssignmentService,
                           UiSessionContext sessionContext,
                           PageModel model) {


        Patient patient = patientService.getPatientByUuid(patientUuid);
        model.put("patient", patient);

        VisitDomainWrapper activeVisit = getActiveVisit(patient, adtService, sessionContext);
        model.put("activeVisit", activeVisit);
        model.addAttribute("wardAndBed", activeVisit == null ? null : bedAssignmentService.getAssignedWardAndBedFor(activeVisit.getVisit()));

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

    public static VisitDomainWrapper getActiveVisit(Patient patient, AdtService adtService, UiSessionContext sessionContext) {
        Location visitLocation = null;
        try {
            visitLocation = adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
        } catch (IllegalArgumentException ex) {
            // location does not support visits
        }
        return visitLocation == null ? null : adtService.getActiveVisit(patient, visitLocation);
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
                           @SpringBean BedAssignmentService bedAssignmentService) {
        try {
            bedAssignmentService.assign(patient, location);
            return new SuccessResult("Transferred");
        } catch (APIException e) {
            return new FailureResult(e.getMessage());
        }
    }
}
