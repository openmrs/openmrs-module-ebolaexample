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

@Component("ebolaDemoData")
@Requires(EbolaMetadata.class)
public class EbolaDemoData extends AbstractMetadataBundle {

    @Autowired
    LocationService locationService;

    public static class _Location {
        public static final String EBOLA_TREATMENT_UNIT = "c035d67e-5830-11e4-af12-660e112eb3f5";
        public static final String OBSERVATION_AREA_1 = "534fed82-5831-11e4-af12-660e112eb3f5";
        public static final String OBSERVATION_AREA_2 = "56a6886a-5831-11e4-af12-660e112eb3f5";
        public static final String HOT_ZONE_AREA_1 = "ed5c93e0-5830-11e4-af12-660e112eb3f5";
        public static final String HOT_ZONE_AREA_2 = "05b6cc12-5831-11e4-af12-660e112eb3f5";
        public static final String HOT_ZONE_AREA_3 = "0fd9102e-5831-11e4-af12-660e112eb3f5";
        public static final String HOT_ZONE_AREA_4 = "1ef1761e-5831-11e4-af12-660e112eb3f5";
        public static final String HOT_ZONE_AREA_5 = "22e1204e-5831-11e4-af12-660e112eb3f5";
    }

    @Override
    public void install() throws Exception {
        String supportsAdmission = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION).getUuid();
        String supportsTransfer = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER).getUuid();

        List<String> tagsForRootLocation = Arrays.asList(
                EbolaMetadata._LocationTag.VISIT_LOCATION,
                AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID
        );
        List<String> tagsForObservationArea = Arrays.asList(
                supportsAdmission,
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_OBSERVATION_AREA
        );
        List<String> tagsForHotZone = Arrays.asList(
                supportsAdmission,
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_HOT_ZONE_AREA
        );

        install(location("Ebola Treatment Unit", "Top level demo location", _Location.EBOLA_TREATMENT_UNIT, null,
                tagsForRootLocation));

        install(location("Observation Area 1", null, _Location.OBSERVATION_AREA_1, _Location.EBOLA_TREATMENT_UNIT,
                tagsForObservationArea));
        install(location("Observation Area 2", null, _Location.OBSERVATION_AREA_2, _Location.EBOLA_TREATMENT_UNIT,
                tagsForObservationArea));

        install(location("Hot Zone Area 1", null, _Location.HOT_ZONE_AREA_1, _Location.EBOLA_TREATMENT_UNIT,
                tagsForHotZone));
        install(location("Hot Zone Area 2", null, _Location.HOT_ZONE_AREA_2, _Location.EBOLA_TREATMENT_UNIT,
                tagsForHotZone));
        install(location("Hot Zone Area 3", null, _Location.HOT_ZONE_AREA_3, _Location.EBOLA_TREATMENT_UNIT,
                tagsForHotZone));
        install(location("Hot Zone Area 4", null, _Location.HOT_ZONE_AREA_4, _Location.EBOLA_TREATMENT_UNIT,
                tagsForHotZone));
        install(location("Hot Zone Area 5", null, _Location.HOT_ZONE_AREA_5, _Location.EBOLA_TREATMENT_UNIT,
                tagsForHotZone));
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
