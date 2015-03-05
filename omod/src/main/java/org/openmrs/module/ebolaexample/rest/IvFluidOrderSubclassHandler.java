package org.openmrs.module.ebolaexample.rest;

import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.domain.AdministrationType;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.OrderResource1_10;

//@SubClassHandler(supportedClass = IvFluidOrder.class, supportedOpenmrsVersions = {"1.10.*"})
public class IvFluidOrderSubclassHandler extends BaseDelegatingSubclassHandler<Order, IvFluidOrder> {
    @Override
    public String getTypeName() {
        return "ivfluidorder";
    }

    @Override
    public IvFluidOrder newDelegate() {
        IvFluidOrder o = new IvFluidOrder();
        o.setOrderType(Context.getOrderService().getOrderTypeByUuid(EbolaMetadata._OrderType.IV_FLUID_ORDER_TYPE_UUID));
        return o;
    }

    @Override
    public PageableResult getAllByType(RequestContext requestContext) throws ResourceDoesNotSupportOperationException {
        throw new ResourceDoesNotSupportOperationException();
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            OrderResource1_10 orderResource = (OrderResource1_10) Context.getService(RestService.class)
                    .getResourceBySupportedClass(Order.class);
            DelegatingResourceDescription d = orderResource.getRepresentationDescription(rep);
            d.addProperty("display");
            d.addProperty("route", Representation.REF);
            d.addProperty("administrationType");
            d.addProperty("bolusQuantity");
            d.addProperty("bolusUnits", Representation.REF);
            d.addProperty("infusionRate");
            d.addProperty("infusionRateNumeratorUnit", Representation.REF);
            d.addProperty("infusionRateDenominatorUnit", Representation.REF);
            d.addProperty("duration");
            d.addProperty("durationUnits", Representation.REF);
            d.addProperty("comments");
            return d;
        }
        return null;
    }

    @Override
    public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
        OrderResource1_10 orderResource = (OrderResource1_10) Context.getService(RestService.class)
                .getResourceBySupportedClass(Order.class);
        DelegatingResourceDescription d = orderResource.getCreatableProperties();
        d.addProperty("display");
        d.addProperty("route");
        d.addProperty("administrationType");
        d.addProperty("bolusQuantity");
        d.addProperty("bolusUnits");
        d.addProperty("infusionRate");
        d.addProperty("infusionRateNumeratorUnit");
        d.addProperty("infusionRateDenominatorUnit");
        d.addProperty("duration");
        d.addProperty("durationUnits");
        d.addProperty("comments");
        return d;
    }

    @PropertyGetter("display")
    public static String getDisplay(IvFluidOrder delegate) {
        if (delegate.getConcept() != null) {
            String ret = delegate.getConcept().getName().toString();

            if (delegate.getAdministrationType() == AdministrationType.BOLUS) {
                ret = ret + ": " + delegate.getBolusQuantity() + " " + delegate.getBolusUnits() + " over";
            } else {
                ret = ret + ": " + delegate.getInfusionRate() + " " +
                        delegate.getInfusionRateNumeratorUnit() + "/" + delegate.getInfusionRateDenominatorUnit() + " for";
            }

            ret = ret + delegate.getDuration() + " " + delegate.getDurationUnits();
            return ret;
        } else {
            return "[no fluid]";
        }
    }
}
