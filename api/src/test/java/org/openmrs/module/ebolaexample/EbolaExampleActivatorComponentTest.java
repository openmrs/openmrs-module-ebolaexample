package org.openmrs.module.ebolaexample;

import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EbolaExampleActivatorComponentTest extends BaseModuleContextSensitiveTest {

    @Test
    public void testStarted() throws Exception {
        new EbolaExampleActivator().started();
    }

}