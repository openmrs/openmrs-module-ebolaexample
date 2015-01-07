package org.openmrs.module.ebolaexample.rest;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;
import java.util.List;

public abstract class ReadableDelegatingResource<T> extends BaseDelegatingResource<T> implements Retrievable, Listable, Converter<T> {

    @Override
    public List<Representation> getAvailableRepresentations() {
        return Arrays.asList(Representation.REF, Representation.DEFAULT);
    }

    @Override
    protected void delete(T delegate, String reason, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public void purge(T delegate, RequestContext context) throws ResponseException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public T save(T delegate) {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public Object retrieve(String uuid, RequestContext context) throws ResponseException {
        T delegate = getByUniqueId(uuid);
        return asRepresentation(delegate, context.getRepresentation());
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.Listable#getAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public SimpleObject getAll(RequestContext context) throws ResponseException {
        if (context.getType() != null) {
            if (!hasTypesDefined())
                throw new IllegalArgumentException(getClass() + " does not support "
                        + RestConstants.REQUEST_PROPERTY_FOR_TYPE);
            if (context.getType().equals(getResourceName()))
                throw new IllegalArgumentException("You may not specify " + RestConstants.REQUEST_PROPERTY_FOR_TYPE + "="
                        + context.getType() + " because it is the default behavior for this resource");
            DelegatingSubclassHandler<T, ? extends T> handler = getSubclassHandler(context.getType());
            if (handler == null)
                throw new IllegalArgumentException("No handler is specified for " + RestConstants.REQUEST_PROPERTY_FOR_TYPE
                        + "=" + context.getType());
            PageableResult result = handler.getAllByType(context);
            return result.toSimpleObject(this);
        } else {
            PageableResult result = doGetAll(context);
            return result.toSimpleObject(this);
        }
    }

    public PageableResult doGetAll(RequestContext context) {
        throw new ResourceDoesNotSupportOperationException();
    }
}
