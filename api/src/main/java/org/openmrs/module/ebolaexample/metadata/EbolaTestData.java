package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.*;
import org.openmrs.api.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.domain.AdministrationType;
import org.openmrs.module.ebolaexample.domain.IvFluidOrder;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.openmrs.module.metadatadeploy.bundle.Requires;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is assumed to run only against a new disposable database (i.e. an in-memory test db), and it does
 * things not normally allowed in a Metadata Deploy package, like creating users (without checking if they already
 * exist).
 */
@Component
@Requires(EbolaMetadata.class)
public class EbolaTestData extends AbstractMetadataBundle {

    public static final String DOCTOR_PROVIDER_IDENTIFIER = "12345";

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private PersonService personService;

    @Autowired
    private OrderService orderService;

    @Autowired @Qualifier("adminService")
    AdministrationService administrationService;

    public static class _Location {
        public static final String EBOLA_TREATMENT_CENTER = "c035d67e-5830-11e4-af12-660e112eb3f5";

        public static final String SUSPECT_WARD = "534fed82-5831-11e4-af12-660e112eb3f5";
        public static final String CONFIRMED_WARD = "ed5c93e0-5830-11e4-af12-660e112eb3f5";
        public static final String RECOVERY_WARD = "0fd9102e-5831-11e4-af12-660e112eb3f5";

        public static final String SUSPECT_BED_1 = "08afd00c-7d2b-11e4-a1ff-1439e1bf193f";
        public static final String CONFIRMED_BED_1 = "0e47c89e-7d2b-11e4-a1ff-1439e1bf193f";
        public static final String CONFIRMED_BED_2 = "12ad63c6-7d2b-11e4-a1ff-1439e1bf193f";
        public static final String RECOVERY_BED_1 = "17a7bffc-7d2b-11e4-a1ff-1439e1bf193f";
    }

    @Override
    public void install() throws Exception {
        administrationService.saveGlobalProperty(new GlobalProperty(EmrApiConstants.GP_ADMISSION_ENCOUNTER_TYPE, EbolaMetadata._EncounterType.EBOLA_TREATMENT_ADMISSION));


        String supportsAdmission = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION).getUuid();
        String supportsTransfer = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER).getUuid();
        String inpatientBed = EbolaMetadata._LocationTag.INPATIENT_BED;

        install(location("Ebola Treatment Unit", "Entire facility", _Location.EBOLA_TREATMENT_CENTER, null,
                EbolaMetadata._LocationTag.VISIT_LOCATION));

        install(location("Suspect Ward", null, _Location.SUSPECT_WARD, _Location.EBOLA_TREATMENT_CENTER,
                supportsAdmission, supportsTransfer, EbolaMetadata._LocationTag.EBOLA_SUSPECT_WARD));
        install(location("Bed #1", null, _Location.SUSPECT_BED_1, _Location.SUSPECT_WARD,
                supportsTransfer, inpatientBed));

        install(location("Confirmed Ward", null, _Location.CONFIRMED_WARD, _Location.EBOLA_TREATMENT_CENTER,
                supportsAdmission, supportsTransfer, EbolaMetadata._LocationTag.EBOLA_CONFIRMED_WARD));
        install(location("Bed #1", null, _Location.CONFIRMED_BED_1, _Location.CONFIRMED_WARD,
                supportsTransfer, inpatientBed));
        install(location("Bed #2", null, _Location.CONFIRMED_BED_2, _Location.CONFIRMED_WARD,
                supportsTransfer, inpatientBed));

        install(location("Recovery Ward", null, _Location.RECOVERY_WARD, _Location.EBOLA_TREATMENT_CENTER,
                supportsTransfer, EbolaMetadata._LocationTag.EBOLA_RECOVERY_WARD));
        install(location("Bed #1", null, _Location.RECOVERY_BED_1, _Location.RECOVERY_WARD,
                supportsTransfer, inpatientBed));

        createProvider(new PersonName("MoH", "", "Doctor"), DOCTOR_PROVIDER_IDENTIFIER);
    }

    public IvFluidOrder createOrder(Patient patient, String orderTypeUuid, String fluidUuid, boolean active) {
        OrderType ivFluidOrderType = Context.getOrderService().getOrderTypeByUuid(orderTypeUuid);

        IvFluidOrder ivFluidOrder = new IvFluidOrder();
        ivFluidOrder.setOrderType(ivFluidOrderType);
        ivFluidOrder.setConcept(getConceptByUuid(fluidUuid));
        ivFluidOrder.setRoute(getConceptByUuid("160242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        ivFluidOrder.setAdministrationType(AdministrationType.BOLUS);
        ivFluidOrder.setBolusQuantity(50.0);
        ivFluidOrder.setBolusUnits(getConceptByUuid("162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        ivFluidOrder.setBolusRate(15);
        ivFluidOrder.setBolusRateUnits(getConceptByUuid("1733AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        ivFluidOrder.setVoided(!active);
        ivFluidOrder.setPatient(patient);
        ivFluidOrder.setCareSetting(Context.getOrderService().getCareSetting(2));
        ivFluidOrder.setEncounter(Context.getEncounterService().getEncountersByPatient(patient).get(0));
        ivFluidOrder.setOrderer(Context.getProviderService().getAllProviders().get(0));
        orderService.saveOrder(ivFluidOrder, null);
        assert ivFluidOrder.getId() != null;
        return ivFluidOrder;
    }

    private Concept getConceptByUuid(String uuid) {
        return Context.getConceptService().getConceptByUuid(uuid);
    }

    private void createProvider(PersonName name, String providerIdentifier) {
        Person person = new Person();
        person.addName(name);
        person.setGender("F");
        personService.savePerson(person);

        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setIdentifier(providerIdentifier);
        providerService.saveProvider(provider);
    }

    public static Location location(String name, String description, String uuid, String parentLocationUuid, String... tagUuids) {
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
