package org.openmrs.module.ebolaexample.importer;

import org.junit.Test;

public class DrugImporterTest {

    @Test
    public void testPrintJsonObjects() throws Exception {
        DrugImporter drugImporter = new DrugImporter();
        drugImporter.printJson();
    }

}