package org.openmrs.module.ebolaexample.importer;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.concept.EmrConceptService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DrugImporter {

    @Autowired
    private EmrConceptService emrConceptService;

    @Autowired
    private ConceptService conceptService;

    public DrugImporter() {

    }

    private ImportNotes verifySpreadsheetHelper(List<DrugImporterRow> drugList) throws IOException {
        ImportNotes notes = new ImportNotes();
        HashSet productNames = new HashSet();
        Iterator i$ = drugList.iterator();

        while (i$.hasNext()) {
            DrugImporterRow row = (DrugImporterRow) i$.next();

            if (productNames.contains(row.getName())) {
                notes.addError("Duplicate drug formulation Name: " + row.getName());
            }

            Drug existingDrug = this.conceptService.getDrug(row.getName());
            if (existingDrug != null && row.getUuid() != null && !existingDrug.getUuid().equals(row.getUuid())) {
                notes.addError("Product already exists in database with different Uuid: " + row.getName() + " (" + existingDrug.getUuid() + ")");
            }

            productNames.add(row.getName());

            if (row.getGenericName() != null) {
                Concept productName = this.emrConceptService.getConcept(row.getGenericName());
                if (productName == null) {
                    notes.addError("Specified concept not found: " + row.getGenericName());
                } else {
                    notes.addNote(row.getGenericName() + " -> " + productName.getId());
                }
            } else {
                String productName1 = row.getName();
                String genericName = productName1.split(",")[0].trim();
                List possibleConcepts = this.conceptService.getConceptsByName(genericName);
                if (possibleConcepts.size() == 0) {
                    notes.addError("At " + productName1 + ", cannot find concept named " + genericName);
                } else if (possibleConcepts.size() > 1) {
                    notes.addError("At " + productName1 + ", found multiple candidate concepts named " + genericName + ": " + possibleConcepts);
                } else {
                    notes.addNote(productName1 + " -> (auto-mapped) " + ((ConceptName) ((Concept) possibleConcepts.get(0)).getNames().iterator().next()).getName());
                }
            }
        }

        return notes;
    }

    public ImportNotes importSpreadsheet(InputStreamReader reader) throws IOException {

        List drugList = this.readSpreadsheet(reader);

        ImportNotes notes = this.verifySpreadsheetHelper(drugList);

        if (notes.hasErrors()) {
            return notes;
        } else {
            Iterator i$ = drugList.iterator();

            while (i$.hasNext()) {
                DrugImporterRow row = (DrugImporterRow) i$.next();
                Drug drug = getDrug(row);
                this.conceptService.saveDrug(drug);
            }

            return notes;
        }
    }

    private Drug getDrug(DrugImporterRow row) {

        Drug drug = null;

        if (row.getUuid() != null) {
            drug = this.conceptService.getDrugByUuid(row.getUuid());
        }

        if (drug == null) {
            drug = this.conceptService.getDrug(row.getName());
        }

        if (drug == null) {
            drug = new Drug();
        }

        if (row.getUuid() != null) {
            drug.setUuid(row.getUuid());
        }

        Concept formConcept = getConceptByName(row.getForm());
        drug.setDosageForm(formConcept);

        Concept concept = getConceptByName(row.getGenericName());
        drug.setConcept(concept);

        drug.setName(row.getName());

        drug.setCombination(row.isCombination());

        drug.setStrength(row.getStrength());

        Concept dosageFormConcept = getConceptByName(row.getForm());
        drug.setDosageForm(dosageFormConcept);

        Concept routeConcept = getConceptByName(row.getRoute());
        drug.setRoute(routeConcept);

        drug.setUnits(row.getDefaultDosageUnits());
        return drug;
    }

    private Concept getConceptByName(String conceptName) {
        Concept concept = this.emrConceptService.getConcept(conceptName);
        if (concept == null) {
            throw new RuntimeException("Specified concept not found: " + conceptName);
        }
        return concept;
    }

    private List<DrugImporterRow> readSpreadsheet(Reader csvFileReader) throws IOException {
        ArrayList drugList = new ArrayList();

        BufferedReader bufferedReader = null;
        String line = "";

        try {
            bufferedReader = new BufferedReader(csvFileReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] drugArray = line.split(",");

                DrugImporterRow drugImporterRow = new DrugImporterRow(drugArray[0], drugArray[1],
                        drugArray[2].equalsIgnoreCase("yes") ? true : false, drugArray[3], drugArray[4],
                        drugArray[5], drugArray[6], drugArray[7]);

                System.out.println("Drug [name= " + drugArray[0]
                        + " , description=" + drugArray[1]
                        + " , isCombination=" + drugArray[2]
                        + " , Strength=" + drugArray[3]
                        + " , Form=" + drugArray[4]
                        + " , Route=" + drugArray[5]
                        + " , Default Dosage Units=" + drugArray[6]
                        + " , UUID=" + drugArray[7] + "]");

                drugList.add(drugImporterRow);
            }

        } catch (Exception e) {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return drugList;
    }
}
