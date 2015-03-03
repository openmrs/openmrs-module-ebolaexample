package org.openmrs.module.ebolaexample;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.visit.VisitDomainWrapper;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Date;

public class PatientLocationUtil {

    public static Location getCurrentWard(VisitDomainWrapper activeVisit) {
        Location currentLocation = null;
        if (activeVisit != null) {
            currentLocation = activeVisit.getInpatientLocation(new Date());
        }

        Location currentWard = closestLocationWithTag(currentLocation,
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_SUSPECT_WARD),
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_CONFIRMED_WARD),
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_RECOVERY_WARD));

        if (currentWard == null) {
            currentWard = currentLocation;
        }
        return currentWard;
    }

    public static Location getCurrentBed(VisitDomainWrapper activeVisit) {
        Location currentLocation = null;
        if (activeVisit != null) {
            currentLocation = activeVisit.getInpatientLocation(new Date());
        }

        return closestLocationWithTag(currentLocation,
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.INPATIENT_BED));
    }


    private static Location closestLocationWithTag(Location location, LocationTag... tags) {
        if (location == null) {
            return null;
        }
        for (LocationTag tag : tags) {
            if (location.hasTag(tag.getName())) {
                return location;
            }
        }
        return closestLocationWithTag(location.getParentLocation(), tags);
    }

}
