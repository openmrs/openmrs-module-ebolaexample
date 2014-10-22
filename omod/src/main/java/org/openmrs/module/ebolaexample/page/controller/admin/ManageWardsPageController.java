package org.openmrs.module.ebolaexample.page.controller.admin;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.ebolaexample.metadata.EbolaDemoData;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.Arrays;
import java.util.List;

public class ManageWardsPageController {

    public void get(@SpringBean("locationService") LocationService locationService,
                    UiUtils ui,
                    PageModel model) {

        String supportsAdmission = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION).getUuid();
        String supportsTransfer = locationService.getLocationTagByName(EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER).getUuid();
        List<String> tagsForAll = Arrays.asList(
                supportsAdmission,
                supportsTransfer
        );

        List<LocationTag> tags = Arrays.asList(
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_OBSERVATION_AREA),
                MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_HOT_ZONE_AREA)
        );

        Object existing = ConversionUtil.convertToRepresentation(locationService.getAllLocations(), Representation.FULL);

        model.addAttribute("tagsForAllJson", ui.toJson(tagsForAll));
        model.addAttribute("parentLocation", MetadataUtils.existing(Location.class, EbolaDemoData._Location.EBOLA_TREATMENT_UNIT));
        model.addAttribute("tags", tags);
        model.addAttribute("locationsJson", ui.toJson(existing));
    }

}
