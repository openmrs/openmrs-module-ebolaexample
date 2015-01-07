package org.openmrs.module.ebolaexample.api;

import org.openmrs.Location;

public class WardAndBed {

    private Location ward;
    private Location bed;

    public WardAndBed() {
    }

    public WardAndBed(Location ward, Location bed) {
        this.ward = ward;
        this.bed = bed;
    }

    public Location getWard() {
        return ward;
    }

    public void setWard(Location ward) {
        this.ward = ward;
    }

    public Location getBed() {
        return bed;
    }

    public void setBed(Location bed) {
        this.bed = bed;
    }
}
