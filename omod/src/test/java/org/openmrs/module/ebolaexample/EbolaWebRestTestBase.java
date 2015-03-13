package org.openmrs.module.ebolaexample;

import org.junit.Before;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

public class EbolaWebRestTestBase extends EbolaWebTestBase {
    @Autowired
    private WebMethods webMethods;

    private MockHttpServletResponse response;

    @Before
    public void setUp() throws Exception{
        super.setUp();
        response = new MockHttpServletResponse();
    }

}
