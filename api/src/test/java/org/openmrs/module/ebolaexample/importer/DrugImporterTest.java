package org.openmrs.module.ebolaexample.importer;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DrugImporterTest {


    @Test
    public void testShouldStripStringOfLeadingQuotes() throws Exception {
        String str = "\"able";
        DrugImporter drugImporter = new DrugImporter();
        assertEquals(drugImporter.cleanString(str), "able");

        str = "\"able\"";
        assertEquals(drugImporter.cleanString(str), "able");
    }

    @Test
    public void shouldKeepQuotedStringsIntact() throws Exception {
        String str = "this,is,a,\"kept, intact\",string";
        DrugImporter drugImporter = new DrugImporter();
        ArrayList<String> strings = drugImporter.splitLine(str);

        assertEquals(strings.get(0), "this");
        assertEquals(strings.get(1), "is");
        assertEquals(strings.get(2), "a");
        assertEquals(strings.get(3), "kept, intact");
        assertEquals(strings.get(4), "string");

        str = "Cefixime,Cefixime 200mg Tablet,,200 mg,Tablet,Oral,\"tablets, mg\",Tier 1,,,";
        strings = drugImporter.splitLine(str);

        assertEquals(strings.get(0), "Cefixime");
        assertEquals(strings.get(1), "Cefixime 200mg Tablet");
        assertEquals(strings.get(2), "");
        assertEquals(strings.get(3), "200 mg");
        assertEquals(strings.get(4), "Tablet");
        assertEquals(strings.get(5), "Oral");
        assertEquals(strings.get(6), "tablets, mg");
        assertEquals(strings.get(7), "Tier 1");
        assertEquals(strings.get(8), "");
        assertEquals(strings.get(9), "");
        assertEquals(strings.get(10), "");
    }

    @Test
    public void shouldReturnCorrectNumberOfStrings() throws Exception {
        String str = "Vitamin A,\"Vitamin A 200,000 unit Capsule\",,\"200,000 unit\",Capsule,Oral,capsules,Tier 1,,\"1) same capsule comment as above, 2) should default dose unit be capsule, tablet, or unit?\",";
        ArrayList<String> strings = new DrugImporter().splitLine(str);
        assertEquals(strings.size(), 11);
    }
}