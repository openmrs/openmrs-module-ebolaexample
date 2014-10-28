package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component("ebolaDemoData")
@Requires(EbolaMetadata.class)
public class EbolaDemoData extends AbstractMetadataBundle {

    @Autowired
    LocationService locationService;

    public static class _Location {
        public static final String EBOLA_TREATMENT_UNIT = "c035d67e-5830-11e4-af12-660e112eb3f5";

        public static final String INPATIENT_WARDS = "b6739628-5e82-11e4-9305-df58197607bd";

        public static final String SUSPECT_WARD_1 = "534fed82-5831-11e4-af12-660e112eb3f5";
        public static final String SUSPECT_WARD_2 = "56a6886a-5831-11e4-af12-660e112eb3f5";
        public static final String SUSPECT_WARD_3 = "1ef1761e-5831-11e4-af12-660e112eb3f5";

        public static final String CONFIRMED_WARD_1 = "ed5c93e0-5830-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD_2 = "05b6cc12-5831-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD_3 = "22e1204e-5831-11e4-af12-660e112eb3f5";

        public static final String RECOVERY_WARD_1 = "0fd9102e-5831-11e4-af12-660e112eb3f5";
    }

    @Override
    public void install() throws Exception {
        String supportsAdmission = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION).getUuid();
        String supportsTransfer = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER).getUuid();

        List<String> tagsForRootLocation = Arrays.asList(
                EbolaMetadata._LocationTag.VISIT_LOCATION
        );
        List<String> tagsForInpatientRoot = Arrays.asList(
                AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID
        );
        List<String> tagsForSuspectWard = Arrays.asList(
                supportsAdmission,
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_SUSPECT_WARD
        );
        List<String> tagsForConfirmedWard = Arrays.asList(
                supportsAdmission,
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_CONFIRMED_WARD
        );
        List<String> tagsForRecoveryWard = Arrays.asList(
                supportsAdmission,
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_RECOVERY_WARD
        );
        List<String> tagsForInpatientBed = Arrays.asList(
                supportsAdmission,
                supportsTransfer,
                EbolaMetadata._LocationTag.INPATIENT_BED
        );

        install(location("Ebola Treatment Unit", "Top level demo location", _Location.EBOLA_TREATMENT_UNIT, null,
                tagsForRootLocation));

        install(location("Inpatient Wards", "Area within which all Inpatient Wards are contained", _Location.INPATIENT_WARDS, _Location.EBOLA_TREATMENT_UNIT,
                tagsForInpatientRoot));

        install(location("Suspect Ward 1", null, _Location.SUSPECT_WARD_1, _Location.INPATIENT_WARDS,
                tagsForSuspectWard));
        install(location("Suspect Ward 2", null, _Location.SUSPECT_WARD_2, _Location.INPATIENT_WARDS,
                tagsForSuspectWard));
        install(location("Suspect Ward 3", null, _Location.SUSPECT_WARD_3, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));

        install(location("Confirmed Ward 1", null, _Location.CONFIRMED_WARD_1, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));
        install(location("Confirmed Ward 2", null, _Location.CONFIRMED_WARD_2, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));
        install(location("Confirmed Ward 3", null, _Location.CONFIRMED_WARD_3, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));

        install(location("Recovery Ward 1", null, _Location.RECOVERY_WARD_1, _Location.INPATIENT_WARDS,
                tagsForRecoveryWard));

        installBeds(_Location.SUSPECT_WARD_1, 10, tagsForInpatientBed);
        installBeds(_Location.SUSPECT_WARD_2, 10, tagsForInpatientBed);
        installBeds(_Location.SUSPECT_WARD_3, 5, tagsForInpatientBed);
    }

    private void installBeds(String parentLocation, int numBeds, Collection<String> tags) {
        Location parent = MetadataUtils.existing(Location.class, parentLocation);
        if (parent.getChildLocations().size() == 0) {
            for (int i = 1; i <= numBeds; ++i) {
                install(location("Bed #" + i, null, UUID.randomUUID().toString(), parentLocation, tags));
            }
        }
    }

    // will go in CoreConstructors in a newer version of Metadata Deploy than we depend on now
    public static Location location(String name, String description, String uuid, String parentLocationUuid, Collection<String> tagUuids) {
        Location obj = new Location();
        obj.setName(name);
        obj.setDescription(description);
        obj.setUuid(uuid);
        if (parentLocationUuid != null) {
            obj.setParentLocation(MetadataUtils.existing(Location.class, parentLocationUuid));
        }
        if (tagUuids != null) {
            for (String tagUuid : tagUuids) {
                obj.addTag(MetadataUtils.existing(LocationTag.class, tagUuid));
            }
        }
        return obj;
    }

}
