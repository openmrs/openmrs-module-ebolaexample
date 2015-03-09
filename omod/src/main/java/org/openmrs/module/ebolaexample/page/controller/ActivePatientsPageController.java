package org.openmrs.module.ebolaexample.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.DateUtil;
import org.openmrs.module.ebolaexample.WardBedAssignments;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public class ActivePatientsPageController {

    public void get(@RequestParam(value = "ward", required = false) String wardId,
                    @SpringBean BedAssignmentService bedAssignmentService,
                    PageModel model) {
        Location selectedWard = null;
        if (StringUtils.isNotBlank(wardId)) {
            selectedWard = Context.getLocationService().getLocationByUuid(wardId);
        }
        model.put("selectedWard", selectedWard);

        List<WardBedAssignments> wardBedAssignments  = bedAssignmentService.getAllBedAssignments();
        List<WardBedAssignments> activeAssignments = new ArrayList<WardBedAssignments>();
        List<Location> locations = new ArrayList<Location>();
        for (WardBedAssignments assignment : wardBedAssignments){
            if(assignment.getBedAssignments().size() != 0){

                Location ward = assignment.getWard();
                locations.add(ward);
                if(selectedWard != null && selectedWard.getName().equals(ward.getName())){
                    activeAssignments.add(assignment);
                }
                else if(selectedWard == null) {
                    activeAssignments.add(assignment);
                }
            }
        }

        model.addAttribute("wards", locations);
        model.addAttribute("today", DateUtil.getDateToday());
        model.addAttribute("assignments", activeAssignments);
    }
}
