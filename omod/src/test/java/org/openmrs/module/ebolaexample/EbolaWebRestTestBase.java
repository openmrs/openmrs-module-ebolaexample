package org.openmrs.module.ebolaexample;

import org.junit.Before;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

public class EbolaWebRestTestBase extends EbolaWebTestBase {
    @Autowired
    protected WebMethods webMethods;

    @Before
    public void setUp() throws Exception{
        super.setUp();
    }

}
