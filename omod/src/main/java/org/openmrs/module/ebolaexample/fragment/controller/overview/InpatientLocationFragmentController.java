package org.openmrs.module.ebolaexample.fragment.controller.overview;

import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.ebolaexample.PatientLocationUtil;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
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

    public void controller(@FragmentParam(value = "activeVisit", required = false) VisitDomainWrapper activeVisit,
                           FragmentModel model) {
        Location currentWard = PatientLocationUtil.getCurrentWard(activeVisit);
        Location currentBed = PatientLocationUtil.getCurrentBed(activeVisit);
        model.addAttribute("currentWard", currentWard);
        model.addAttribute("currentBed", currentBed);
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
