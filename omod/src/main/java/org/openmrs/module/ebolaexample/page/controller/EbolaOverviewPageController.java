package org.openmrs.module.ebolaexample.page.controller;

import org.apache.commons.lang.time.DateFormatUtils;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.context.AppContextModel;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.appui.UiSessionContext;
import org.openmrs.module.coreapps.contextmodel.PatientContextModel;
import org.openmrs.module.coreapps.contextmodel.VisitContextModel;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.event.ApplicationEventService;
import org.openmrs.module.emrapi.patient.PatientDomainWrapper;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EbolaOverviewPageController {

    public void get(@RequestParam("patient") Patient patient,
                    @InjectBeans PatientDomainWrapper patientDomainWrapper,
                    @SpringBean AdtService adtService,
                    @SpringBean AppFrameworkService appFrameworkService,
                    @SpringBean("applicationEventService") ApplicationEventService applicationEventService,
                    @SpringBean("programWorkflowService") ProgramWorkflowService programWorkflowService,
                    @SpringBean BedAssignmentService bedAssignmentService,
                    UiSessionContext sessionContext,
                    UiUtils ui,
                    PageModel model) {

        patientDomainWrapper.setPatient(patient);

        VisitDomainWrapper activeVisit = getActiveVisit(patient, adtService, sessionContext);

        Program program = MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM);
        EncounterType followupEncounterType = MetadataUtils.existing(EncounterType.class, EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);
        EncounterType triageEncounterType = MetadataUtils.existing(EncounterType.class, EbolaMetadata._EncounterType.EBOLA_TRIAGE);

        Map<String, Object> formParams = new HashMap<String, Object>();
        formParams.put("patientId", patient.getUuid());
        formParams.put("visitId", activeVisit == null ? null : activeVisit.getVisit().getUuid());
        formParams.put("definitionUiResource", "ebolaexample:htmlforms/inpatientObservationsAndTreatment.xml");
        formParams.put("returnUrl", ui.thisUrl());
        // TODO breadcrumbOverride

        //Map<String, String> followupForms = new LinkedHashMap<String, String>();
        //followupForms.put("Add", ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi", formParams));

        model.addAttribute("program", program);
        model.addAttribute("followupEncounterType", followupEncounterType);
        //model.addAttribute("followupForms", followupForms);
        model.addAttribute("triageEncounterType", triageEncounterType);
        model.addAttribute("patient", patientDomainWrapper);
        model.addAttribute("activeVisit", activeVisit);
        model.addAttribute("wardAndBed", activeVisit == null ? null : bedAssignmentService.getAssignedWardAndBedFor(activeVisit.getVisit()));

        if (activeVisit != null) {
            model.addAttribute("activeVisitStartDatetime",
                    DateFormatUtils.format(activeVisit.getStartDatetime(), "dd MMM yyyy hh:mm a", Context.getLocale()));
        }
        AppContextModel contextModel = sessionContext.generateAppContextModel();
        contextModel.put("patient", new PatientContextModel(patient));
        contextModel.put("visit", activeVisit == null ? null : new VisitContextModel(activeVisit));
        model.addAttribute("appContextModel", contextModel);

        // TODO use custom extension point, not the standard patient dashboard one
        List<Extension> visitActions;
        if (activeVisit == null) {
            visitActions = new ArrayList<Extension>();
        } else {
            visitActions = appFrameworkService.getExtensionsForCurrentUser("patientDashboard.visitActions", contextModel);
            Collections.sort(visitActions);
        }
        model.addAttribute("visitActions", visitActions);

        // TODO use custom extension point, not the standard patient dashboard one
        List<Extension> overallActions = appFrameworkService.getExtensionsForCurrentUser("patientDashboard.overallActions", contextModel);
        Collections.sort(overallActions);
        model.addAttribute("overallActions", overallActions);

        // TODO maybe make this a custom extension point; for now this is probably fine
        List<Extension> includeFragments = appFrameworkService.getExtensionsForCurrentUser("patientDashboard.includeFragments");
        Collections.sort(includeFragments);
        model.addAttribute("includeFragments", includeFragments);

        PatientProgram patientProgram = ChangePatientDischargePageController.getPatientProgram(patient, programWorkflowService);
        model.put("patientProgram", patientProgram);
        model.put("currentOutcome", patientProgram != null ? patientProgram.getOutcome() : null);

        applicationEventService.patientViewed(patient, sessionContext.getCurrentUser());
    }

    private VisitDomainWrapper getActiveVisit(Patient patient, AdtService adtService, UiSessionContext sessionContext) {
        Location visitLocation = null;
        try {
            visitLocation = adtService.getLocationThatSupportsVisits(sessionContext.getSessionLocation());
        } catch (IllegalArgumentException ex) {
            // location does not support visits
        }
        return visitLocation == null ? null : adtService.getActiveVisit(patient, visitLocation);
    }

}
