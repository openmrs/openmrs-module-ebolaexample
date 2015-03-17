package org.openmrs.module.ebolaexample;

import org.junit.Before;
import org.openmrs.module.ebolaexample.rest.WebMethods;
import org.springframework.beans.factory.annotation.Autowired;

public class EbolaRestTestBase extends EbolaTestBase {
    @Autowired
    protected WebMethods webMethods;

    @Before
    public void setUp() throws Exception{
        super.setUp();
    }

}
