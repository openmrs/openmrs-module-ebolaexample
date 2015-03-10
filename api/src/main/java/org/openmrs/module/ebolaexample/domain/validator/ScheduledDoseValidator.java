package org.openmrs.module.ebolaexample.domain.validator;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ebolaexample.domain.ScheduledDose;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { ScheduledDose.class}, order = 50)
public class ScheduledDoseValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ScheduledDose.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ScheduledDose dose = (ScheduledDose) target;
        ValidationUtils.rejectIfEmpty(errors, "order", "Required");
        ValidationUtils.rejectIfEmpty(errors, "scheduledDatetime", "Required");
        ValidationUtils.rejectIfEmpty(errors, "status", "Required");

        if (!errors.hasErrors()) {
            if (ScheduledDose.DoseStatus.FULL.equals(dose.getStatus())
                    && StringUtils.isNotEmpty(dose.getReasonNotAdministeredNonCoded())) {
                errors.rejectValue("reasonNotAdministeredNonCoded", "Not allowed if dose is fully given");
            }

            if (OpenmrsUtil.compare(dose.getScheduledDatetime(), dose.getOrder().getEffectiveStartDate()) < 0) {
                errors.rejectValue("scheduledDatetime", "Cannot be before prescription start date");
            }
            if (OpenmrsUtil.compareWithNullAsLatest(dose.getScheduledDatetime(), dose.getOrder().getEffectiveStopDate()) > 0) {
                errors.rejectValue("scheduledDatetime", "Cannot be after prescription stop date");
            }
        }
    }

}
