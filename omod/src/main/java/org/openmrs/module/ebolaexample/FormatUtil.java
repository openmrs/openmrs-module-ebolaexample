package org.openmrs.module.ebolaexample;

import org.openmrs.DrugOrder;
import org.openmrs.SimpleDosingInstructions;
import org.openmrs.ui.framework.UiUtils;

import java.util.Locale;

public class FormatUtil {

    /**
     * We need custom formatting for SimpleDosingInstructions since that core class doesn't know to use the
     * Ebola-preferred concept names (e.g. it shows "Milliliter" instead of "mL")
     */
    public static String formatPrescription(Object o, Locale locale, UiUtils ui) {
        DrugOrder order = (DrugOrder) o;

        if (order.getDosingType().equals(SimpleDosingInstructions.class)) {
            SimpleDosingInstructions instructions = (SimpleDosingInstructions) order.getDosingInstructionsInstance();
            // copied from SimpleDosingInstructions in core, and just changes the formatting of concepts
            StringBuilder dosingInstructions = new StringBuilder();
            dosingInstructions.append(instructions.getDose());
            dosingInstructions.append(" ");
            dosingInstructions.append(ui.format(instructions.getDoseUnits()));
            dosingInstructions.append(" ");
            dosingInstructions.append(ui.format(instructions.getRoute()));
            dosingInstructions.append(" ");
            dosingInstructions.append(ui.format(instructions.getFrequency().getConcept()));
            if (order.getDuration() != null) {
                dosingInstructions.append(" ");
                dosingInstructions.append(instructions.getDuration());
                dosingInstructions.append(" ");
                dosingInstructions.append(ui.format(instructions.getDurationUnits()));
            }
            if (order.getAsNeeded()) {
                dosingInstructions.append(" ");
                dosingInstructions.append("PRN");
                if (order.getAsNeededCondition() != null) {
                    dosingInstructions.append(" ");
                    dosingInstructions.append(instructions.getAsNeededCondition());
                }
            }
            if (instructions.getAdministrationInstructions() != null) {
                dosingInstructions.append(" ");
                dosingInstructions.append(instructions.getAdministrationInstructions());
            }
            return dosingInstructions.toString();
        }
        else {
            return order.getDosingInstructionsInstance().getDosingInstructionsAsString(locale);
        }
    }

}
