package org.openmrs.module.ebolaexample.page.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.DateUtil;
import org.openmrs.module.ebolaexample.WardBedAssignments;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

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

        Map<Location, List<SimpleObject>> sortedAssignments = getSortedAssignments(activeAssignments);
        model.addAttribute("wards", locations);
        model.addAttribute("today", DateUtil.getDateToday());
        model.addAttribute("assignments", sortedAssignments);
    }

    private Map<Location, List<SimpleObject>> getSortedAssignments(List<WardBedAssignments> activeAssignments) {
        Map<Location, List<SimpleObject>> sortedAssignments = new HashMap<Location, List<SimpleObject>>();
        for(WardBedAssignments assignment: activeAssignments){
            List<SimpleObject> assignments = getSortedAssignmentsByBed(assignment);
            sortedAssignments.put(assignment.getWard(), assignments);
        }
        return sortedAssignments;
    }

    private List<SimpleObject> getSortedAssignmentsByBed(WardBedAssignments assignment) {
        List<SimpleObject> assignments = new ArrayList<SimpleObject>();
        for (Map.Entry<Location, Patient> entry : assignment.getBedAssignments().entrySet()) {
            SimpleObject item = new SimpleObject();
            item.put("bed", ConversionUtil.convertToRepresentation(entry.getKey(), Representation.REF));
            item.put("patient", ConversionUtil.convertToRepresentation(entry.getValue(), Representation.REF));
            assignments.add(item);
        }
        Collections.sort(assignments, new Comparator<SimpleObject>() {
            @Override
            public int compare(SimpleObject left, SimpleObject right) {
                return index(left).compareTo(index(right));
            }

            private Integer index(SimpleObject input) {
                SimpleObject bed = (SimpleObject) input.get("bed");
                String name = (String) bed.get("display");
                String number = name.substring(name.indexOf("#") + 1);
                return Integer.valueOf(number);
            }
        });
        return assignments;
    }
}
