package org.openmrs.module.ebolaexample.domain;

import org.openmrs.DosingInstructions;
import org.openmrs.DrugOrder;
import org.openmrs.FreeTextDosingInstructions;
import org.openmrs.api.APIException;
import org.springframework.validation.Errors;

import java.util.Locale;

public class UnvalidatedFreeTextDosingInstructions extends FreeTextDosingInstructions {

    @Override
    public DosingInstructions getDosingInstructions(DrugOrder order) throws APIException {
        if (!order.getDosingType().equals(this.getClass())) {
            throw new APIException("Dosing type of drug order is mismatched. Expected:" + this.getClass() + " but received:"
                    + order.getDosingType());
        }
        UnvalidatedFreeTextDosingInstructions instructions = new UnvalidatedFreeTextDosingInstructions();
        instructions.setInstructions(order.getDosingInstructions());
        return instructions;
    }

    @Override
    public void validate(DrugOrder order, Errors errors) {
    }

    @Override
    public String getDosingInstructionsAsString(Locale locale) {
        String instructions = this.getInstructions();
        return instructions == null ? "" : instructions;
    }

}
