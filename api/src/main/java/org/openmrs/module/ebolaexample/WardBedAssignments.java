package org.openmrs.module.ebolaexample;

import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.Map;

public class WardBedAssignments {

	private Location ward;

	private Map<Location, Patient> bedAssignments;

	public WardBedAssignments() {
	}

	public WardBedAssignments(Location ward, Map<Location, Patient> bedAssignments) {
		this.ward = ward;
		this.bedAssignments = bedAssignments;
	}

	public Location getWard() {
		return ward;
	}

	public void setWard(Location ward) {
		this.ward = ward;
	}

	public Map<Location, Patient> getBedAssignments() {
		return bedAssignments;
	}

	public void setBedAssignments(Map<Location, Patient> bedAssignments) {
		this.bedAssignments = bedAssignments;
	}
}
