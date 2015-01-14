package org.openmrs.module.ebolaexample.importer;

public class DrugImporterRow {

    private String uuid;
    private String genericName;
    private String name;
    private boolean combination;
    private String strength;
    private String form;
    private String route;
    private String defaultDosageUnits;

    public DrugImporterRow(){

    }

    public DrugImporterRow(String genericName, String name, boolean combination, String strength,
                           String form, String route, String defaultDosageUnits, String uuid) {
        this.genericName = genericName;
        this.name = name;
        this.combination = combination;
        this.strength = strength;
        this.form = form;
        this.route = route;
        this.defaultDosageUnits = defaultDosageUnits;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCombination() {
        return combination;
    }

    public void setCombination(boolean combination) {
        this.combination = combination;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDefaultDosageUnits() {
        return defaultDosageUnits;
    }

    public void setDefaultDosageUnits(String defaultDosageUnits) {
        this.defaultDosageUnits = defaultDosageUnits;
    }
}
