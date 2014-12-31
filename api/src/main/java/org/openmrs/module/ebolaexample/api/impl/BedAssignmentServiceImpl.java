package org.openmrs.module.ebolaexample.api.impl;

import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.LocationTag;
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
import org.openmrs.module.ebolaexample.api.WardAndBed;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.adt.AdtAction;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    @Transactional(readOnly = true)
    public Patient getPatientAssignedTo(Location bed) {
        VisitAttributeType assignedBed = MetadataUtils.existing(VisitAttributeType.class, EbolaMetadata._VisitAttributeType.ASSIGNED_BED);
        Location visitLocation = adtService.getLocationThatSupportsVisits(bed);
        for (VisitDomainWrapper visit : adtService.getActiveVisits(visitLocation)) {
            for (VisitAttribute visitAttribute : visit.getVisit().getActiveAttributes(assignedBed)) {
                if (bed.equals(visitAttribute.getValue())) {
                    return visit.getVisit().getPatient();
                }
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public WardAndBed getAssignedWardAndBedFor(Visit visit) {
        VisitAttributeType assignedWard = MetadataUtils.existing(VisitAttributeType.class, EbolaMetadata._VisitAttributeType.ASSIGNED_WARD);
        VisitAttributeType assignedBed = MetadataUtils.existing(VisitAttributeType.class, EbolaMetadata._VisitAttributeType.ASSIGNED_BED);

        Location ward = getAttribute(assignedWard, visit);
        Location bed = ward == null ? null : getAttribute(assignedBed, visit);
        return new WardAndBed(ward, bed);
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

        Patient currentlyAssignedPatient = getPatientAssignedTo(bed);
        if (currentlyAssignedPatient != null) {
            if (currentlyAssignedPatient.equals(patient)) {
                // patient is already assigned to this bed, so we do nothing
                return;
            }
            else {
                throw new APIException("The bed already has a patient assigned");
            }
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
    public List<Location> getAllWards() {
        List<Location> wards = new ArrayList<Location>();
        wards.addAll(locationService.getLocationsByTag(MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_SUSPECT_WARD)));
        wards.addAll(locationService.getLocationsByTag(MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_CONFIRMED_WARD)));
        wards.addAll(locationService.getLocationsByTag(MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_RECOVERY_WARD)));
        return wards;
    }

    @Override
    @Transactional(readOnly=true)
    public List<WardBedAssignments> getAllBedAssignments() {
        List<Location> wards = getAllWards();
        Set<Location> facilities = new HashSet<Location>();
        Map<Location, Map<Location, Patient>> bedAssignments = new HashMap<Location, Map<Location, Patient>>();
        for (Location ward : wards) {
            facilities.add(adtService.getLocationThatSupportsVisits(ward));
            bedAssignments.put(ward, new HashMap<Location, Patient>());
        }

        VisitAttributeType assignedWard = visitService.getVisitAttributeTypeByUuid(
                EbolaMetadata._VisitAttributeType.ASSIGNED_WARD);
        VisitAttributeType assignedBed = visitService.getVisitAttributeTypeByUuid(
                EbolaMetadata._VisitAttributeType.ASSIGNED_BED);

        for (Location facility : facilities) {
            for (VisitDomainWrapper visit : adtService.getActiveVisits(facility)) {
                Location ward = getAttribute(assignedWard, visit);
                Location bed = getAttribute(assignedBed, visit);
                if (bed != null) {
                    Patient patient = visit.getVisit().getPatient();
                    bedAssignments.get(ward).put(bed, patient);
                }
            }
        }

        List<WardBedAssignments> ret = new ArrayList<WardBedAssignments>();
        for (Location ward : wards) {
            ret.add(new WardBedAssignments(ward, bedAssignments.get(ward)));
        }
        return ret;
    }

    @Override
	@Transactional(readOnly=true)
	public WardBedAssignments getBedAssignments(Location ward) {
        VisitAttributeType assignedWard = visitService.getVisitAttributeTypeByUuid(
                EbolaMetadata._VisitAttributeType.ASSIGNED_WARD);
        VisitAttributeType assignedBed = visitService.getVisitAttributeTypeByUuid(
                EbolaMetadata._VisitAttributeType.ASSIGNED_BED);

        Map<Location, Patient> bedAssignments = new HashMap<Location, Patient>();

        Location facility = adtService.getLocationThatSupportsVisits(ward);
        for (VisitDomainWrapper visit : adtService.getActiveVisits(facility)) {
            if (ward.equals(getAttribute(assignedWard, visit))) {
                Location bed = getAttribute(assignedBed, visit);
                Patient patient = visit.getVisit().getPatient();
                bedAssignments.put(bed, patient);
            }
        }
		return new WardBedAssignments(ward, bedAssignments);
	}

    private Location getAttribute(VisitAttributeType assignedWardType, VisitDomainWrapper visit) {
        return getAttribute(assignedWardType, visit.getVisit());
    }

    private Location getAttribute(VisitAttributeType attributeType, Visit visit) {
        Location assignedWard = null;
        List<VisitAttribute> activeAttributes = visit.getActiveAttributes(attributeType);
        if (activeAttributes.size() > 0) {
            assignedWard = (Location) activeAttributes.get(0).getValue();
        }
        return assignedWard;
    }

}
