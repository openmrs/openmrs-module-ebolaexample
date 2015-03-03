package org.openmrs.module.ebolaexample.domain;

import org.openmrs.Concept;
import org.openmrs.Order;

public class IvFluidOrder extends Order {
    private Concept route;

    private AdministrationType administrationType;

    private Double bolusQuantity;

    private Concept bolusUnits;

    private Double infusionRate;

    private Concept infusionRateNumeratorUnit;

    private Concept infusionRateDenominatorUnit;

    private Integer duration;

    private Concept durationUnits;

    private String comments;

    public IvFluidOrder copy() {
        return copyHelper(new IvFluidOrder());
    }

    /**
     * @see org.openmrs.Order#copyHelper(Order)
     */
    protected IvFluidOrder copyHelper(IvFluidOrder target) {
        super.copyHelper(target);
        target.setRoute(getRoute());
        target.setAdministrationType(getAdministrationType());
        target.setBolusQuantity(getBolusQuantity());
        target.setBolusUnits(getBolusUnits());
        target.setInfusionRate(getInfusionRate());
        target.setInfusionRateNumeratorUnit(getInfusionRateNumeratorUnit());
        target.setInfusionRateDenominatorUnit(getInfusionRateDenominatorUnit());
        target.setDuration(getDuration());
        target.setDurationUnits(getDurationUnits());
        target.setComments(getComments());
        return target;
    }

    public Concept getRoute() {
        return route;
    }

    public void setRoute(Concept route) {
        this.route = route;
    }

    public AdministrationType getAdministrationType() {
        return administrationType;
    }

    public void setAdministrationType(AdministrationType administrationType) {
        this.administrationType = administrationType;
    }

    public Double getBolusQuantity() {
        return bolusQuantity;
    }

    public void setBolusQuantity(Double bolusQuantity) {
        this.bolusQuantity = bolusQuantity;
    }

    public Concept getBolusUnits() {
        return bolusUnits;
    }

    public void setBolusUnits(Concept bolusUnits) {
        this.bolusUnits = bolusUnits;
    }

    public Double getInfusionRate() {
        return infusionRate;
    }

    public void setInfusionRate(Double infusionRate) {
        this.infusionRate = infusionRate;
    }

    public Concept getInfusionRateNumeratorUnit() {
        return infusionRateNumeratorUnit;
    }

    public void setInfusionRateNumeratorUnit(Concept infusionRateNumeratorUnit) {
        this.infusionRateNumeratorUnit = infusionRateNumeratorUnit;
    }

    public Concept getInfusionRateDenominatorUnit() {
        return infusionRateDenominatorUnit;
    }

    public void setInfusionRateDenominatorUnit(Concept infusionRateDenominatorUnit) {
        this.infusionRateDenominatorUnit = infusionRateDenominatorUnit;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Concept getDurationUnits() {
        return durationUnits;
    }

    public void setDurationUnits(Concept durationUnits) {
        this.durationUnits = durationUnits;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}