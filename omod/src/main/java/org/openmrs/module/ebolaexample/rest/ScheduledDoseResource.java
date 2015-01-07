package org.openmrs.module.ebolaexample.rest;

import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.api.PharmacyService;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/ebola/scheduled-dose", supportedClass = ScheduledDose.class, supportedOpenmrsVersions = "1.10.*")
public class ScheduledDoseResource extends DelegatingCrudResource<ScheduledDose> implements Retrievable {

    @Override
    public ScheduledDose getByUniqueId(String uuid) {
        return Context.getService(PharmacyService.class).getScheduledDoseByUuid(uuid);
    }

    @Override
    protected void delete(ScheduledDose delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public void purge(ScheduledDose delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public ScheduledDose newDelegate() {
        return new ScheduledDose();
    }

    @Override
    public ScheduledDose save(ScheduledDose delegate) {
        PharmacyService service = Context.getService(PharmacyService.class);
        delegate.setDateCreated(new Date());
        delegate.setCreator(Context.getAuthenticatedUser());
        delegate.setScheduledDatetime(new Date());
        return service.saveScheduledDose(delegate);
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
            description.addProperty("order", Representation.REF);
        }
        description.addSelfLink();
        return description;
    }

    @PropertyGetter("display")
    public String getDisplay(ScheduledDose delegate) {
        StringBuilder display = new StringBuilder();
        display.append(delegate.getStatus());
        if(delegate.getDateCreated() != null) {
            display.append(" - ");
            display.append(delegate.getDateCreated().toString());
        }
        return display.toString();
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription properties = new DelegatingResourceDescription();
        properties.addProperty("status");
        properties.addProperty("reasonNotAdministeredNonCoded");
        properties.addProperty("order");
        return properties;
    }

    @Override
    public List<String> getPropertiesToExposeAsSubResources() {
        return Arrays.asList("order");
    }

}
