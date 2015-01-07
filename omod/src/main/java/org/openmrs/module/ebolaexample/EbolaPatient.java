package org.openmrs.module.ebolaexample;

import org.openmrs.DrugOrder;
import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.List;

public class EbolaPatient {

    private Patient patient;
    private Location ward;
    private Location bed;
    private List<DrugOrder> drugOrders;

    public EbolaPatient(Patient patient) {
        this.patient = patient;
    }

    public List<DrugOrder> getDrugOrders() {
        return drugOrders;
    }

    public void setDrugOrders(List<DrugOrder> drugOrders) {
        this.drugOrders = drugOrders;
    }

    public Patient getPatient() {
        return patient;
    }

    public Location getBed() {
        return bed;
    }

    public void setBed(Location bed) {
        this.bed = bed;
    }

    public Location getWard() {
        return ward;
    }

    public void setWard(Location ward) {
        this.ward = ward;
    }

}
