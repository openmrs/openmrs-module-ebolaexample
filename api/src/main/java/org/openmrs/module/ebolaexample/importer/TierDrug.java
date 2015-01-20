package org.openmrs.module.ebolaexample.importer;

import org.openmrs.Drug;

public class TierDrug {
    private Drug drug = null;
    private String tier;
    private String uuid;

    public TierDrug() {

    }

    public TierDrug(String tier, String uuid) {
        this.tier = tier;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public Drug getDrug() {
        return drug;
    }

    @Override
    public String toString() {
        return tier + " " + (drug != null ? drug.getConcept().getDisplayString() : "Null Drug") + " --  " + uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        TierDrug other = (TierDrug) obj;
        if (this.getUuid() == null) {
            if (other.getUuid() != null)
                return false;
        } else if (!this.getUuid().equals(other.getUuid()))
            return false;
        return true;
    }

    public String getTier() {
        return tier;
    }
}
