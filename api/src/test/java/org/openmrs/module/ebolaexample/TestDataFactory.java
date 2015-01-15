package org.openmrs.module.ebolaexample;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.DrugOrder;
import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.domain.RoundBasedDosingInstructions;

import java.util.Random;

public class TestDataFactory {

    public static DrugOrder createValidDrugOrder() {
        DrugOrder drugOrder = new DrugOrder();
        drugOrder.setDose(10.0);
        drugOrder.setDoseUnits(createConceptWithName("ml"));
        drugOrder.setRoute(createConceptWithName("IV"));
        drugOrder.setDosingInstructions("Morning, Evening");
        drugOrder.setDosingType(RoundBasedDosingInstructions.class);
        drugOrder.setDuration(5);
        drugOrder.setDurationUnits(createConceptWithName("days"));
        OrderFrequency frequency = new OrderFrequency();
        frequency.setConcept(createConceptWithName("Twice a day"));
        drugOrder.setFrequency(frequency);
        return drugOrder;
    }

    public static Concept createConceptWithName(String name) {
        Concept concept = new Concept(new Random().nextInt());
        ConceptName conceptName = new ConceptName();
        conceptName.setName(name);
        conceptName.setLocale(Context.getLocale());
        conceptName.setLocalePreferred(true);
        concept.addName(conceptName);
        return concept;
    }
}
