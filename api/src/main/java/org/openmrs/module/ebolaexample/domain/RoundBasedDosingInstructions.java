package org.openmrs.module.ebolaexample.domain;

import org.openmrs.*;
import org.openmrs.api.APIException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class RoundBasedDosingInstructions implements DosingInstructions {

    private Double dose;

    private Concept doseUnits;

    private Concept route;

    private Integer duration;

    private Concept durationUnits;

    private Boolean asNeeded;

    private String asNeededCondition;

    private String roundInstructions;

    @Override
    public String getDosingInstructionsAsString(Locale locale) {
        StringBuilder dosingInstructions = new StringBuilder();
        dosingInstructions.append(this.dose);
        dosingInstructions.append(" ");
        dosingInstructions.append(this.doseUnits.getName(locale).getName());
        dosingInstructions.append(" ");
        dosingInstructions.append(this.route.getName(locale).getName());
        if (duration != null) {
            dosingInstructions.append(" ");
            dosingInstructions.append(this.duration);
            dosingInstructions.append(" ");
            dosingInstructions.append(this.durationUnits.getName(locale).getName());
        }
        if (this.asNeeded) {
            dosingInstructions.append(" ");
            dosingInstructions.append("PRN");
            if (this.asNeededCondition != null) {
                dosingInstructions.append(" ");
                dosingInstructions.append(this.asNeededCondition);
            }
        }
        dosingInstructions.append(" ");
        dosingInstructions.append(this.roundInstructions);
        return dosingInstructions.toString();
    }

    @Override
    public void setDosingInstructions(DrugOrder order) {
        order.setDosingType(this.getClass());
        order.setDose(this.dose);
        order.setDoseUnits(this.doseUnits);
        order.setRoute(this.route);
        order.setDuration(this.duration);
        order.setDurationUnits(this.durationUnits);
        order.setAsNeeded(this.asNeeded);
        order.setAsNeededCondition(this.asNeededCondition);
        order.setDosingInstructions(this.roundInstructions);
    }

    @Override
    public DosingInstructions getDosingInstructions(DrugOrder order) {
        if (!order.getDosingType().equals(this.getClass())) {
            throw new APIException("Dosing type of drug order is mismatched. Expected:" + this.getClass().getName()
                    + " but received:" + order.getDosingType());
        }
        RoundBasedDosingInstructions roundBasedDosingInstructions = new RoundBasedDosingInstructions();
        roundBasedDosingInstructions.setDose(order.getDose());
        roundBasedDosingInstructions.setDoseUnits(order.getDoseUnits());
        roundBasedDosingInstructions.setDuration(order.getDuration());
        roundBasedDosingInstructions.setDurationUnits(order.getDurationUnits());
        roundBasedDosingInstructions.setAsNeeded(order.getAsNeeded());
        roundBasedDosingInstructions.setAsNeededCondition(order.getAsNeededCondition());
        roundBasedDosingInstructions.setRoute(order.getRoute());
        roundBasedDosingInstructions.setRoundInstructions(order.getDosingInstructions());
        return roundBasedDosingInstructions;
    }

    @Override
    public void validate(DrugOrder order, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "dosingInstructions", "DrugOrder.error.dosingInstructionsIsNullForDosingTypeRoundBased");
        ValidationUtils.rejectIfEmpty(errors, "route", "DrugOrder.error.routeIsNullForDosingTypeRoundBased");
        ValidationUtils.rejectIfEmpty(errors, "dose", "DrugOrder.error.doseIsNullForDosingTypeRoundBased");
        ValidationUtils.rejectIfEmpty(errors, "doseUnits", "DrugOrder.error.doseUnitsIsNullForDosingTypeRoundBased");
        ValidationUtils.rejectIfEmpty(errors, "duration", "DrugOrder.error.durationIsNullForDosingTypeRoundBased");

        String[] rounds = order.getDosingInstructions().split(",");
        for (String round : rounds) {
            boolean validRoundName = Arrays.asList(new String[]{"Morning", "Afternoon", "Evening", "Night"}).contains(round);
            if(!validRoundName) {
                errors.rejectValue("dosingInstructions", "DrugOrder.error.dosingInstructionsInAnInvalidFormat");
            }
        }
    }

    @Override
    public Date getAutoExpireDate(DrugOrder order) {
        return null;
    }

    public void setDose(Double dose) {
        this.dose = dose;
    }

    public void setDoseUnits(Concept doseUnits) {
        this.doseUnits = doseUnits;
    }

    public void setRoute(Concept route) {
        this.route = route;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setDurationUnits(Concept durationUnits) {
        this.durationUnits = durationUnits;
    }

    public void setAsNeeded(Boolean asNeeded) {
        this.asNeeded = asNeeded;
    }

    public void setAsNeededCondition(String asNeededCondition) {
        this.asNeededCondition = asNeededCondition;
    }

    public void setRoundInstructions(String administrationInstructions) {
        this.roundInstructions = administrationInstructions;
    }
}
