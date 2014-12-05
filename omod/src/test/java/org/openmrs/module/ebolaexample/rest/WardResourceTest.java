package org.openmrs.module.ebolaexample.rest;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class WardResourceTest extends BaseModuleWebContextSensitiveTest {

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Autowired
    private EbolaTestData ebolaTestData;

    private WardResource resource;

    @Before
    public void setUp() throws Exception {
        ebolaTestData.install();
        ebolaMetadata.install();
        resource = new WardResource();
    }

    @Test
    public void testGetAll() throws Exception {
        RequestContext requestContext = new RequestContext();
        SimpleObject all = resource.getAll(requestContext);
        System.out.println(all);
    }

}