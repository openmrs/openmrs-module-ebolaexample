package org.openmrs.module.ebolaexample;

import org.openmrs.DrugOrder;
import org.openmrs.Patient;

import java.util.List;

public class EbolaPatient {

    private Patient patient;
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
}
