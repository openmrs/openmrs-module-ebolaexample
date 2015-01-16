package org.openmrs.module.ebolaexample.domain;

import org.openmrs.Concept;
import org.openmrs.ConceptNameTag;
import org.openmrs.DosingInstructions;
import org.openmrs.DrugOrder;
import org.openmrs.SimpleDosingInstructions;
import org.openmrs.api.APIException;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.uiframework.ConceptFormatter;
import org.openmrs.module.metadatadeploy.MetadataUtils;
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
        ConceptNameTag preferredTag = MetadataUtils.existing(ConceptNameTag.class, EbolaMetadata._ConceptNameTag.PREFERRED);
        StringBuilder dosingInstructions = new StringBuilder();
        dosingInstructions.append(prettyDouble(this.dose));
        dosingInstructions.append(" ");
        dosingInstructions.append(ConceptFormatter.bestName(preferredTag, locale, this.doseUnits));
        dosingInstructions.append(" ");
        dosingInstructions.append(ConceptFormatter.bestName(preferredTag, locale, this.route));
        dosingInstructions.append(" each ");
        dosingInstructions.append(this.roundInstructions);
        if (duration != null) {
            dosingInstructions.append(" for ");
            dosingInstructions.append(this.duration);
            dosingInstructions.append(" ");
            dosingInstructions.append(ConceptFormatter.bestName(preferredTag, locale, this.durationUnits));
        }
        if (this.asNeeded) {
            dosingInstructions.append(" <span class=\"lozenge prn\">");
            dosingInstructions.append("PRN");
            if (this.asNeededCondition != null) {
                dosingInstructions.append(" ");
                dosingInstructions.append(this.asNeededCondition);
            }
            dosingInstructions.append("</span>");
        }
        return dosingInstructions.toString();
    }

    private String prettyDouble(Double dbl) {
        if (dbl == null) {
            return "";
        }
        if (dbl == Math.round(dbl)) {
            return Long.toString(Math.round(dbl));
        }
        else {
            return Double.toString(dbl);
        }
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

        String[] rounds = order.getDosingInstructions().split(", ");
        for (String round : rounds) {
            boolean validRoundName = Arrays.asList(new String[]{"Morning", "Afternoon", "Evening", "Night"}).contains(round);
            if(!validRoundName) {
                errors.rejectValue("dosingInstructions", "DrugOrder.error.dosingInstructionsInAnInvalidFormat");
            }
        }
    }

    @Override
    public Date getAutoExpireDate(DrugOrder order) {
        return new SimpleDosingInstructions().getAutoExpireDate(order);
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
