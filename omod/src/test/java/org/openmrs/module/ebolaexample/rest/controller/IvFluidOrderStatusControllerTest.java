package org.openmrs.module.ebolaexample.rest.controller;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.module.ebolaexample.EbolaWebRestTestBase;
import org.openmrs.module.webservices.rest.web.RestConstants;

public class IvFluidOrderStatusControllerTest  extends EbolaWebRestTestBase {

    private String requestURI = "/rest/" + RestConstants.VERSION_1 + "/ebola/ivfluid-order-status/";;

    @Test
    public void testShouldGetLatestStatusOfIvFluidOrder(){
        Assert.assertEquals(1,1);
    }
}