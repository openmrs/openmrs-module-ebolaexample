package org.openmrs.module.ebolaexample;

import org.junit.Before;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestBaseMetadata;
import org.openmrs.module.ebolaexample.metadata.EbolaTestData;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class EbolaTestBase extends BaseModuleWebContextSensitiveTest {
    @Autowired
    protected EbolaTestBaseMetadata ebolaTestBaseMetadata;

    @Autowired
    protected EbolaMetadata ebolaMetadata;

    @Autowired
    protected EbolaTestData ebolaTestData;

    @Before
    public void setUp()  throws Exception {
        ebolaTestBaseMetadata.install();
        ebolaMetadata.install();
        ebolaTestData.install();
        initializeInMemoryDatabase();
    }
}

