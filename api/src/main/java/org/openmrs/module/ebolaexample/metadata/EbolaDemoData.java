package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component("ebolaDemoData")
@Requires(EbolaMetadata.class)
public class EbolaDemoData extends AbstractMetadataBundle {

    @Autowired
    LocationService locationService;

    public static class _Location {
        public static final String EBOLA_TREATMENT_UNIT = "c035d67e-5830-11e4-af12-660e112eb3f5";

        public static final String TRIAGE = "e0e18fea-6860-11e4-9305-df58197607bd";
        public static final String ASSESSMENT = "d4da0286-6860-11e4-9305-df58197607bd";

        public static final String INPATIENT_WARDS = "b6739628-5e82-11e4-9305-df58197607bd";

        public static final String DRY_MALE_SUSPECT_WARD = "534fed82-5831-11e4-af12-660e112eb3f5";
        public static final String DRY_FEMALE_SUSPECT_WARD = "56a6886a-5831-11e4-af12-660e112eb3f5";
        public static final String WET_MALE_SUSPECT_WARD = "1ef1761e-5831-11e4-af12-660e112eb3f5";
        public static final String WET_FEMALE_SUSPECT_WARD = "04c1857f-5831-11e4-af12-660e112eb3f5";

        public static final String CONFIRMED_WARD_1 = "ed5c93e0-5830-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD_2 = "05b6cc12-5831-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD_3 = "22e1204e-5831-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD_4 = "9f169b18-5831-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD_5 = "99e3d431-5831-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD_6 = "a3df7cf3-5831-11e4-af12-660e112eb3f5";

        public static final String RECOVERY_WARD_1 = "0fd9102e-5831-11e4-af12-660e112eb3f5";
        public static final String RECOVERY_WARD_2 = "3f7af19b-5831-11e4-af12-660e112eb3f5";
    }

    @Override
    public void install() throws Exception {
        String supportsAdmission = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION).getUuid();
        String supportsTransfer = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER).getUuid();

        List<String> tagsForRootLocation = Arrays.asList(
                EbolaMetadata._LocationTag.VISIT_LOCATION
        );
        List<String> tagsForTriage = Arrays.asList();
        List<String> tagsForAssessment = Arrays.asList(
                supportsAdmission,
                AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID
        );
        List<String> tagsForInpatientRoot = Arrays.asList(
                AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN_UUID
        );
        List<String> tagsForSuspectWard = Arrays.asList(
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_SUSPECT_WARD
        );
        List<String> tagsForConfirmedWard = Arrays.asList(
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_CONFIRMED_WARD
        );
        List<String> tagsForRecoveryWard = Arrays.asList(
                supportsTransfer,
                EbolaMetadata._LocationTag.EBOLA_RECOVERY_WARD
        );
        List<String> tagsForInpatientBed = Arrays.asList(
                supportsTransfer,
                EbolaMetadata._LocationTag.INPATIENT_BED
        );

        install(location("Ebola Treatment Unit", "Top level demo location", _Location.EBOLA_TREATMENT_UNIT, null,
                tagsForRootLocation));

        install(location("Triage", "To determine if they should be admitted or sent home", _Location.TRIAGE, _Location.EBOLA_TREATMENT_UNIT,
                tagsForTriage));

        install(location("Assessment", "Patients admitted at triage are sent to this location to determine what ward to send them to", _Location.ASSESSMENT, _Location.EBOLA_TREATMENT_UNIT,
                tagsForAssessment));

        install(location("Inpatient Wards", "Area within which all Inpatient Wards are contained", _Location.INPATIENT_WARDS, _Location.EBOLA_TREATMENT_UNIT,
                tagsForInpatientRoot));

        install(location("Dry Male Suspect Ward", null, _Location.DRY_MALE_SUSPECT_WARD, _Location.INPATIENT_WARDS,
                tagsForSuspectWard));
        install(location("Dry Female Suspect Ward", null, _Location.DRY_FEMALE_SUSPECT_WARD, _Location.INPATIENT_WARDS,
                tagsForSuspectWard));
        install(location("Wet Male Suspect Ward", null, _Location.WET_MALE_SUSPECT_WARD, _Location.INPATIENT_WARDS,
                tagsForSuspectWard));
        install(location("Wet Female Suspect Ward", null, _Location.WET_FEMALE_SUSPECT_WARD, _Location.INPATIENT_WARDS,
                tagsForSuspectWard));

        install(location("Confirmed Ward 1", null, _Location.CONFIRMED_WARD_1, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));
        install(location("Confirmed Ward 2", null, _Location.CONFIRMED_WARD_2, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));
        install(location("Confirmed Ward 3", null, _Location.CONFIRMED_WARD_3, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));
        install(location("Confirmed Ward 4", null, _Location.CONFIRMED_WARD_4, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));
        install(location("Confirmed Ward 5", null, _Location.CONFIRMED_WARD_5, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));
        install(location("Confirmed Ward 6", null, _Location.CONFIRMED_WARD_6, _Location.INPATIENT_WARDS,
                tagsForConfirmedWard));

        install(location("Recovery Ward 1", null, _Location.RECOVERY_WARD_1, _Location.INPATIENT_WARDS,
                tagsForRecoveryWard));
        install(location("Recovery Ward 2", null, _Location.RECOVERY_WARD_2, _Location.INPATIENT_WARDS,
                tagsForRecoveryWard));

        installBeds(_Location.DRY_MALE_SUSPECT_WARD, 5, tagsForInpatientBed);
        installBeds(_Location.DRY_FEMALE_SUSPECT_WARD, 5, tagsForInpatientBed);
        installBeds(_Location.WET_MALE_SUSPECT_WARD, 5, tagsForInpatientBed);
        installBeds(_Location.WET_FEMALE_SUSPECT_WARD, 5, tagsForInpatientBed);
        installBeds(_Location.CONFIRMED_WARD_1, 10, tagsForInpatientBed);
        installBeds(_Location.CONFIRMED_WARD_2, 10, tagsForInpatientBed);
        installBeds(_Location.CONFIRMED_WARD_3, 10, tagsForInpatientBed);
        installBeds(_Location.CONFIRMED_WARD_4, 10, tagsForInpatientBed);
        installBeds(_Location.CONFIRMED_WARD_5, 10, tagsForInpatientBed);
        installBeds(_Location.CONFIRMED_WARD_6, 10, tagsForInpatientBed);
        installBeds(_Location.RECOVERY_WARD_1, 6, tagsForInpatientBed);
        installBeds(_Location.RECOVERY_WARD_2, 6, tagsForInpatientBed);

        installUser(buildTeam("1"), "Team1234");
        installUser(buildTeam("2"), "Team1234");
        installUser(buildTeam("3"), "Team1234");
    }

    private void installUser(User user, String password) {
        if(Context.getUserService().getUserByUsername(user.getUsername()) == null) {
            Context.getUserService().saveUser(user, password);
        }
    }

    private User buildTeam(String teamNumber) {
        User user = new User();
        HashSet<Role> roles = new HashSet<Role>();
        roles.add(Context.getUserService().getRole(EbolaMetadata._Role.WARD_ROUNDING_TEAM));
        user.setRoles(roles);
        user.setUsername("Team" + teamNumber);
        user.setName("Team " + teamNumber);
        user.setDescription("Team " + teamNumber);
        Person person = buildPersonForTeam(teamNumber);
        user.setPerson(person);
        return user;
    }

    private Person buildPersonForTeam(String teamNumber) {
        Person person = new Person();
        person.setGender("Female");
        person.addName(new PersonName("Team " + teamNumber, "", ""));
        return person;
    }

    private void installBeds(String parentLocation, int numBeds, Collection<String> tags) {
        Location parent = MetadataUtils.existing(Location.class, parentLocation);
        List<Location> existingChildren = locationService.getLocations(null, parent, null, false, null, null);
        if (existingChildren.size() == 0) {
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
        Set<LocationTag> tags = new HashSet<LocationTag>();
        if (tagUuids != null) {
            for (String tagUuid : tagUuids) {
                tags.add(MetadataUtils.existing(LocationTag.class, tagUuid));
            }
        }
        obj.setTags(tags);
        return obj;
    }

}
