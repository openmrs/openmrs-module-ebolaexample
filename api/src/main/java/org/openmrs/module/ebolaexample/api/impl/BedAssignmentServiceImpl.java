package org.openmrs.module.ebolaexample.api.impl;

import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.VisitAttributeType;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ebolaexample.WardBedAssignments;
import org.openmrs.module.ebolaexample.api.BedAssignmentService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BedAssignmentServiceImpl extends BaseOpenmrsService implements BedAssignmentService {

	private AdtService adtService;
	private ProviderService providerService;
	private EncounterService encounterService;
	private VisitService visitService;
	private LocationService locationService;
	private AdministrationService administrationService;

	public void setAdtService(AdtService adtService) {
		this.adtService = adtService;
	}

	public void setProviderService(ProviderService providerService) {
		this.providerService = providerService;
	}

	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}

	public void setVisitService(VisitService visitService) {
		this.visitService = visitService;
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

	public void setAdministrationService(AdministrationService administrationService) {
		this.administrationService = administrationService;
	}

	@Override
	@Transactional
	public void assign(Patient patient, Location bed) {
		Map<EncounterRole, Set<Provider>> providers = new HashMap<EncounterRole, Set<Provider>>();
		providers.put(MetadataUtils.existing(EncounterRole.class, EbolaMetadata._EncounterRole.CLINICIAN),
				new HashSet<Provider>(providerService.getProvidersByPerson(
						Context.getAuthenticatedUser().getPerson())));

		VisitDomainWrapper activeVisit = adtService.getActiveVisit(patient, bed);

		if (activeVisit == null) {
			throw new APIException("No active visit");
		} else if (!activeVisit.isAdmitted()) {
			throw new APIException("Not admitted (so cannot transfer)");
		} else if (!bed.getTags().contains(locationService.getLocationTagByUuid(EbolaMetadata._LocationTag.INPATIENT_BED))) {
			throw new APIException("Bed doesn't have the \"Inpatient Bed\" LocationTag");
		}  else if (bed.getParentLocation() == null) {
			throw new APIException("The bed is not assigned to a ward");
		}
		EncounterType transferEncounterType = encounterService.getEncounterTypeByUuid(
				administrationService.getGlobalProperty(EmrApiConstants.GP_TRANSFER_WITHIN_HOSPITAL_ENCOUNTER_TYPE));
		if (encounterService.getEncounters(null, bed, null, null, null,
				Arrays.asList(transferEncounterType), null, null, null, false).size() > 0) {
			throw new APIException("The bed already has a patient assigned");
		}
		Visit visit = activeVisit.getVisit();

		VisitAttributeType assignedWardType = visitService.getVisitAttributeTypeByUuid(
				EbolaMetadata._VisitAttributeType.ASSIGNED_WARD);
		VisitAttributeType assignedBedType = visitService.getVisitAttributeTypeByUuid(
				EbolaMetadata._VisitAttributeType.ASSIGNED_BED);
		VisitAttribute wardAttribute = new VisitAttribute(), bedAttribute = new VisitAttribute();
		wardAttribute.setAttributeType(assignedWardType);
		bedAttribute.setAttributeType(assignedBedType);
		wardAttribute.setVisit(visit);
		bedAttribute.setVisit(visit);
		wardAttribute.setValue(bed.getParentLocation());
		bedAttribute.setValue(bed);
		Set<VisitAttribute> visitAttributes = visit.getAttributes();
		visitAttributes.removeAll(visit.getActiveAttributes(assignedWardType));
		visitAttributes.removeAll(visit.getActiveAttributes(assignedBedType));
		visitAttributes.addAll(new HashSet<VisitAttribute>(Arrays.asList(wardAttribute, bedAttribute)));
		visit = visitService.saveVisit(visit);

		AdtAction adtAction = new AdtAction(visit, bed, providers, AdtAction.Type.TRANSFER);

		adtService.createAdtEncounterFor(adtAction);
	}

	@Override
	@Transactional(readOnly=true)
	public WardBedAssignments getBedAssignments(Location ward) {
		Map<Location, Patient> bedAssignments = new HashMap<Location, Patient>();
		for(VisitDomainWrapper visit : adtService.getActiveVisits(ward)) {
			Encounter encounter = visit.getLatestAdtEncounter();
			if (encounter != null) {
				bedAssignments.put(encounter.getLocation(), encounter.getPatient());
			}
		}
		return new WardBedAssignments(ward, bedAssignments);
	}

}
