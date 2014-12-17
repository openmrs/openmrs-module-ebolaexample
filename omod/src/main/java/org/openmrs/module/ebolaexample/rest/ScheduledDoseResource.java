package org.openmrs.module.ebolaexample.rest;

import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.api.impl.PharmacyServiceImpl;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.springframework.beans.factory.annotation.Autowired;

@Resource(name = RestConstants.VERSION_1 + "/ebola/scheduled-dose", supportedClass = ScheduledDose.class, supportedOpenmrsVersions = "1.10.*")
public class ScheduledDoseResource extends ReadableDelegatingResource {

    @Override
    public Object getByUniqueId(String uuid) {
        return Context.getService(PharmacyService.class).getScheduledDoseByUuid(uuid);
    }

    @Override
    public ScheduledDose newDelegate() {
        return new ScheduledDose();
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("display");
        if (!rep.equals(Representation.REF)) {
            description.addProperty("status");
            description.addProperty("dateCreated");
            description.addProperty("reasonNotAdministeredNonCoded");
        }
        description.addSelfLink();
        return description;
    }

    @Override
    public PageableResult doGetAll(RequestContext context) {
        return new NeedsPaging<ScheduledDose>(Context.getService(PharmacyService.class).getAllScheduledDoses(), context);
    }

    @PropertyGetter("display")
    public String getDisplay(ScheduledDose delegate) {
        StringBuilder display = new StringBuilder();
        display.append(delegate.getStatus());
        display.append(" - ");
        display.append(delegate.getDateCreated().toString());
        return display.toString();
    }

}
