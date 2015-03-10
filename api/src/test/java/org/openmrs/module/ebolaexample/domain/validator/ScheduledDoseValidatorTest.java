package org.openmrs.module.ebolaexample.domain.validator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScheduledDoseValidatorTest {

    private SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    private ScheduledDoseValidator validator;
    private ScheduledDose dose;
    private Errors errors;

    @Before
    public void setUp() throws Exception {
        validator = new ScheduledDoseValidator();
        dose = new ScheduledDose();
        errors = new BindException(dose, "");
    }

    @Test
    public void shouldPass() throws Exception {
        dose.setOrder(buildOrder());
        dose.setScheduledDatetime(ymd.parse("2015-01-02"));
        dose.setStatus(ScheduledDose.DoseStatus.FULL);

        validator.validate(dose, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void shouldFailIfReasonProvidedForFullDose() throws Exception {
        dose.setOrder(buildOrder());
        dose.setScheduledDatetime(ymd.parse("2014-10-12"));
        dose.setStatus(ScheduledDose.DoseStatus.FULL);
        dose.setReasonNotAdministeredNonCoded("Some reason");

        validator.validate(dose, errors);
        assertTrue(errors.hasFieldErrors("reasonNotAdministeredNonCoded"));
    }

    @Test
    public void shouldFailIfDoseIsBeforeStart() throws Exception {
        dose.setOrder(buildOrder());
        dose.setScheduledDatetime(ymd.parse("2014-10-12"));
        dose.setStatus(ScheduledDose.DoseStatus.FULL);

        validator.validate(dose, errors);
        assertTrue(errors.hasFieldErrors("scheduledDatetime"));
    }

    @Test
    public void shouldFailIfDoseIsAfterEnd() throws Exception {
        dose.setOrder(buildOrder());
        dose.getOrder().setAutoExpireDate(ymd.parse("2015-01-03"));
        dose.setScheduledDatetime(ymd.parse("2015-02-01"));
        dose.setStatus(ScheduledDose.DoseStatus.FULL);

        validator.validate(dose, errors);
        assertTrue(errors.hasFieldErrors("scheduledDatetime"));
    }

    private DrugOrder buildOrder() throws ParseException {
        DrugOrder order = new DrugOrder();
        order.setDrug(new Drug());
        order.setConcept(new Concept());
        order.setDateActivated(ymd.parse("2015-01-02"));
        return order;
    }

}