package org.openmrs.module.ebolaexample.api;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ebolaexample.WardBedAssignments;

public interface BedAssignmentService extends OpenmrsService {

	/**
	 * Creates a transfer encounter; also updates visit attributes (active visit is inferred from patient and location)
	 * Exception if the patient has no active visit
	 * Exception if patient's active visit is not an inpatient one
	 * Exception if bed doesn't have the "Inpatient Bed" LocationTag
	 * Exception if the bed is not assigned to a ward
	 * Exception if the bed already has a patient assigned
	 */
	public void assign(Patient patient, Location bed);

    public Patient getPatientAssignedTo(Location bed);

	public WardBedAssignments getBedAssignments(Location ward);
}
