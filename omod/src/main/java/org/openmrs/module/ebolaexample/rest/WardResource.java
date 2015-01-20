package org.openmrs.module.ebolaexample.rest;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.WardBedAssignments;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Resource(name = RestConstants.VERSION_1 + "/ebola/ward", supportedClass = WardBedAssignments.class, supportedOpenmrsVersions = "1.10.*")
public class WardResource extends ReadableDelegatingResource<WardBedAssignments> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("display");
        description.addProperty("type");
        if (!rep.equals(Representation.REF)) {
            description.addProperty("bedAssignments");
        }
        description.addSelfLink();
        return description;
    }

    @Override
    public WardBedAssignments newDelegate() {
        return new WardBedAssignments();
    }

    @Override
    public WardBedAssignments getByUniqueId(String uuid) {
        Location ward = Context.getLocationService().getLocationByUuid(uuid);
        return Context.getService(BedAssignmentService.class).getBedAssignments(ward);
    }

    @Override
    public PageableResult doGetAll(RequestContext context) throws ResponseException {
        BedAssignmentService bedAssignmentService = Context.getService(BedAssignmentService.class);
        List<WardBedAssignments> wardBedAssignments = bedAssignmentService.getAllBedAssignments();
        return new NeedsPaging<WardBedAssignments>(wardBedAssignments, context);
    }

    @PropertyGetter("uuid")
    public String getUuid(WardBedAssignments delegate) {
        return delegate.getWard().getUuid();
    }

    @PropertyGetter("display")
    public String getDisplay(WardBedAssignments delegate) {
        return delegate.getWard().getName();
    }

    @PropertyGetter("type")
    public String getWardType(WardBedAssignments delegate) {
        Location ward = delegate.getWard();
        if (ward.hasTag(MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_SUSPECT_WARD).getName())) {
            return "suspect";
        }
        else if (ward.hasTag(MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_CONFIRMED_WARD).getName())) {
            return "confirmed";
        }
        else if (ward.hasTag(MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_RECOVERY_WARD).getName())) {
            return "recovery";
        }
        else {
            return null;
        }
    }

    @PropertyGetter("bedAssignments")
    public List<SimpleObject> getBedAssignments(WardBedAssignments delegate) {
        List<SimpleObject> assignments = new ArrayList<SimpleObject>();
        for (Map.Entry<Location, Patient> entry : delegate.getBedAssignments().entrySet()) {
            SimpleObject assignment = new SimpleObject();
            assignment.put("bed", ConversionUtil.convertToRepresentation(entry.getKey(), Representation.REF));
            assignment.put("patient", ConversionUtil.convertToRepresentation(entry.getValue(), Representation.REF));
            assignments.add(assignment);
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
