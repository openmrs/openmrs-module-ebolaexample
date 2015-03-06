package org.openmrs.module.ebolaexample.domain;

import org.openmrs.Concept;
import org.openmrs.Order;

public class IvFluidOrder extends Order {
    private Concept route;

    private AdministrationType administrationType;

    private Double bolusQuantity;

    private Concept bolusUnits;

    private Integer bolusRate;

    private Concept bolusRateUnits;

    private Double infusionRate;

    private Concept infusionRateNumeratorUnit;

    private Concept infusionRateDenominatorUnit;

    private Integer infusionDuration;

    private Concept infusionDurationUnits;

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
        target.setInfusionDuration(getInfusionDuration());
        target.setInfusionDurationUnits(getInfusionDurationUnits());
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

    public Integer getBolusRate() {
        return bolusRate;
    }

    public void setBolusRate(Integer bolusRate) {
        this.bolusRate = bolusRate;
    }

    public Concept getBolusRateUnits() {
        return bolusRateUnits;
    }

    public void setBolusRateUnits(Concept bolusRateUnits) {
        this.bolusRateUnits = bolusRateUnits;
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

    public Integer getInfusionDuration() {
        return infusionDuration;
    }

    public void setInfusionDuration(Integer duration) {
        this.infusionDuration = duration;
    }

    public Concept getInfusionDurationUnits() {
        return infusionDurationUnits;
    }

    public void setInfusionDurationUnits(Concept durationUnits) {
        this.infusionDurationUnits = durationUnits;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}