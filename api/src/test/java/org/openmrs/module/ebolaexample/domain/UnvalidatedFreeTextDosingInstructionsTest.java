package org.openmrs.module.ebolaexample.domain;

import org.junit.Test;
import org.openmrs.DosingInstructions;
import org.openmrs.DrugOrder;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static junit.framework.Assert.assertEquals;

public class UnvalidatedFreeTextDosingInstructionsTest extends BaseModuleContextSensitiveTest {

    @Test
    public void validate_emptyOrdersShouldBeValid() throws Exception {
        DrugOrder drugOrder = new DrugOrder();
        Errors errors = new BindException(drugOrder, "drugOrder");
        new UnvalidatedFreeTextDosingInstructions().validate(drugOrder, errors);
        assertEquals(false, errors.hasErrors());
    }

    @Test
    public void getDosingInstructions_returnsUnvalidatedFreeTextDosingInstructions() {
        DrugOrder drugOrder = new DrugOrder();
        drugOrder.setDosingType(UnvalidatedFreeTextDosingInstructions.class);
        DosingInstructions dosingInstructions = new UnvalidatedFreeTextDosingInstructions().getDosingInstructions(drugOrder);
        assertEquals(dosingInstructions.getClass(), UnvalidatedFreeTextDosingInstructions.class);
    }

    @Test
    public void getDosingInstructions_setsInstructions() {
        String instructions = "Some instructions";
        DrugOrder drugOrder = new DrugOrder();
        drugOrder.setDosingType(UnvalidatedFreeTextDosingInstructions.class);
        drugOrder.setDosingInstructions(instructions);
        UnvalidatedFreeTextDosingInstructions dosingInstructions =
                (UnvalidatedFreeTextDosingInstructions) new UnvalidatedFreeTextDosingInstructions().getDosingInstructions(drugOrder);
        assertEquals(dosingInstructions.getInstructions(), instructions);
    }

    @Test
    public void getDosingInstructionsAsString_returnsEmptyStringsInsteadOfNull() {
        DrugOrder drugOrder = new DrugOrder();
        drugOrder.setDosingType(UnvalidatedFreeTextDosingInstructions.class);
        DosingInstructions dosingInstructions = new UnvalidatedFreeTextDosingInstructions().getDosingInstructions(drugOrder);
        assertEquals("", dosingInstructions.getDosingInstructionsAsString(Context.getLocale()));
    }
}
