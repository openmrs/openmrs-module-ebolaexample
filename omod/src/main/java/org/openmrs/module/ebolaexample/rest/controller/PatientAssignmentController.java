package org.openmrs.module.ebolaexample.rest.controller;


import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.api.WardAndBed;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.ui.framework.SimpleObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/ebola/assignment")
public class PatientAssignmentController extends BaseRestController {

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Object get(@RequestParam String patientUuid){
        SimpleObject response = new SimpleObject();
        PatientService patientService = Context.getPatientService();
        Patient patient = patientService.getPatientByUuid(patientUuid);
        List<Visit> activeVisitsByPatient = Context.getVisitService().getActiveVisitsByPatient(patient);
        BedAssignmentService bedAssignmentService = Context.getService(BedAssignmentService.class);
        WardAndBed assignedWardAndBedFor = bedAssignmentService.getAssignedWardAndBedFor(activeVisitsByPatient.get(0));
        response.put("ward", assignedWardAndBedFor.getWard().getName());
        response.put("bed", assignedWardAndBedFor.getBed().getName());
        return response;
    }
}
