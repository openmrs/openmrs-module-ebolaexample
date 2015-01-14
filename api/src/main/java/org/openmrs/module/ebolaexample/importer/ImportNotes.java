package org.openmrs.module.ebolaexample.importer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImportNotes {

    private List<String> notes = new ArrayList();
    private List<String> errors = new ArrayList();

    public ImportNotes() {
    }

    public List<String> getNotes() {
        return this.notes;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public ImportNotes addNote(String note) {
        this.notes.add(note);
        return this;
    }

    public ImportNotes addError(String error) {
        this.errors.add(error);
        return this;
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Errors ===\n");
        if (this.errors.size() == 0) {
            sb.append("None\n");
        }

        Iterator i$ = this.errors.iterator();

        String note;
        while (i$.hasNext()) {
            note = (String) i$.next();
            sb.append(note).append("\n");
        }

        sb.append("=== Notes ===\n");
        if (this.notes.size() == 0) {
            sb.append("None\n");
        }

        i$ = this.notes.iterator();

        while (i$.hasNext()) {
            note = (String) i$.next();
            sb.append(note).append("\n");
        }

        return sb.toString();
    }

}
