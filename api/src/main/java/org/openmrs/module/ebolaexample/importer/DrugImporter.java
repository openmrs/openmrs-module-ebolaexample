package org.openmrs.module.ebolaexample.importer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Component
public class DrugImporter {

    @Autowired
    private ConceptService conceptService;

    protected Log log = LogFactory.getLog(getClass());

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
                row.setHasError(true);
            }

            Drug existingDrug = this.conceptService.getDrug(row.getName());
            if (existingDrug != null && row.getUuid() != null && !existingDrug.getUuid().equals(row.getUuid())) {
                notes.addError("Product already exists in database with different Uuid: " + row.getName() + " (" + existingDrug.getUuid() + ")");
                row.setHasError(true);
            }

            productNames.add(row.getName());

            if (row.getGenericName() != null) {
                Concept conceptName = this.conceptService.getConceptByName(row.getGenericName());
                if (conceptName == null) {
                    notes.addError(row.getGenericName() + " -> missing concept");
                    row.setHasError(true);
                } else {
                    notes.addNote(row.getGenericName() + " -> " + conceptName.getId());
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

    public ImportNotes importSpreadsheet(Reader reader) throws IOException {

        List drugList = this.readSpreadsheet2(reader);

        ImportNotes notes = this.verifySpreadsheetHelper(drugList);

        if (notes.hasErrors()) {
            log.error(notes);
        }

        Iterator i$ = drugList.iterator();
        while (i$.hasNext()) {
            DrugImporterRow row = (DrugImporterRow) i$.next();
            if (row.hasError()) {
                continue;
            }
            Drug drug = getDrug(row);

            log.debug("xxxxxxxxxx - Saving - " + drug.getName());
            this.conceptService.saveDrug(drug);
        }

        return notes;
    }

    private Drug getDrug(DrugImporterRow row) {

        Drug drug = null;

        if (StringUtils.isNotBlank(row.getUuid())) {
            drug = this.conceptService.getDrugByUuid(row.getUuid());
        }

        if (drug == null) {
            drug = this.conceptService.getDrug(row.getName());
        }

        if (drug == null) {
            drug = new Drug();
        }

        if (StringUtils.isNotBlank(row.getUuid())) {
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
        // route is deprecated, but it's okay for us to use it
        drug.setRoute(routeConcept);

        // TODO do something with row.getDefaultDosageUnits()
        return drug;
    }

    private Concept getConceptByName(String conceptName) {
        return this.conceptService.getConceptByName(conceptName);
    }

    // visible for testing
    List<DrugImporterRow> readSpreadsheet(Reader csvFileReader) throws IOException {
        ArrayList drugList = new ArrayList();

        BufferedReader bufferedReader = null;
        String line = "";

        try {
            bufferedReader = new BufferedReader(csvFileReader);

            while ((line = bufferedReader.readLine()) != null) {
                ArrayList<String> strings = splitLine(line);

                // genericName, name, combination, strength, form, route, defaultDosageUnits, uuid
                String genericName = cleanString(strings.get(0));
                String name = cleanString(strings.get(1));
                String combination = cleanString(strings.get(2));
                String strength = cleanString(strings.get(3));
                String form = cleanString(strings.get(4));
                String route = cleanString(strings.get(5));
                String defaultDosageUnits = cleanString(strings.get(6));
                String tier = cleanString(strings.get(7));
                String uuid = cleanString(strings.get(8));

                DrugImporterRow drugImporterRow = new DrugImporterRow(genericName, name,
                        combination.equalsIgnoreCase("yes") ? true : false, strength, form, route, defaultDosageUnits,
                        uuid, tier);

                drugList.add(drugImporterRow);
            }

        } catch (Exception e) {
            log.error("Error", e);
        } finally {
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

    private CellProcessor[] getCellProcessors() {
        return new CellProcessor[]{new Optional(new Trim()),
                new Optional(new Trim()), new Optional(new ParseBool()),
                new Optional(new Trim()), new Optional(new Trim()),
                new Optional(new Trim()), new Optional(new Trim()),
                new Optional(new Trim()), new Optional(new Trim())};
    }

    List<DrugImporterRow> readSpreadsheet2(Reader csvFileReader) throws IOException {

        ArrayList drugList = new ArrayList();
        CsvBeanReader csv = null;

        try {
            csv = new CsvBeanReader(csvFileReader, CsvPreference.EXCEL_PREFERENCE);
            csv.getHeader(true);
            CellProcessor[] cellProcessors = this.getCellProcessors();

            while (true) {
                DrugImporterRow row = (DrugImporterRow) csv.read(DrugImporterRow.class, DrugImporterRow.FIELD_COLUMNS, cellProcessors);
                if (row == null) {
                    return drugList;
                }

                drugList.add(row);
            }
        } finally {
            if (csv != null) {
                csv.close();
            }
        }
    }

    public ArrayList<String> splitLine(String line) {
        String[] drugArrayWithQuotes = line.split("\"");
        ArrayList<String> strings = new ArrayList<String>();
        for (int i = 0; i < drugArrayWithQuotes.length; i++) {

            String str = drugArrayWithQuotes[i];

            if (str.startsWith(",") && i > 0) {
                str = str.substring(1);
            }
            if (str.endsWith(",") && i < drugArrayWithQuotes.length - 1) {
                str = str.substring(0, str.length() - 1);
            }

            if (line.contains("\"" + str + "\"")) {
                strings.add(str);
            } else {

                if (str.equals("")) {
                    strings.add("");
                    continue;
                }

                String[] splitString = str.split(",", -1);
                for (String s : splitString) {
                    strings.add(s);
                }


            }
        }
        return strings;
    }

    public String cleanString(String str) {
        str = str.trim();
        if (str.startsWith("\"")) {
            str = str.substring(1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public List<TierDrug> getTierDrugs() {

        List<TierDrug> tierDrugs = new ArrayList<TierDrug>();

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Kerry_Town_Drugs.csv");
            InputStreamReader reader = new InputStreamReader(inputStream);

            List<DrugImporterRow> drugList = readSpreadsheet(reader);

            for (DrugImporterRow row : drugList) {
                if (isNotBlankOrEmpty(row.getTier()) && isNotBlankOrEmpty(row.getUuid())) {
                    tierDrugs.add(new TierDrug(row.getTier(), row.getUuid()));
                }
            }
        } catch (Exception e) {
            log.error("Import Error", e);
        }

        return tierDrugs;
    }

    private boolean isNotBlankOrEmpty(String str) {
        return StringUtils.isNotBlank(str) && StringUtils.isNotEmpty(str);
    }
}
