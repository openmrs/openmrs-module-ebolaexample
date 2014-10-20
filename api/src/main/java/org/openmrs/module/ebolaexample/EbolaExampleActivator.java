/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.ebolaexample;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;

import java.util.Arrays;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EbolaExampleActivator extends BaseModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());

    @Override
    public void started() {
        deployMetadataPackages(Context.getService(MetadataDeployService.class));

        setupEmrApiGlobalProperties(Context.getAdministrationService());

        disableApps(Context.getService(AppFrameworkService.class));
        doNotSupportLoginAtUnknownLocation(Context.getLocationService());
        log.info("Started Ebola Example module");
    }

    private void doNotSupportLoginAtUnknownLocation(LocationService locationService) {
        Location location = locationService.getLocation("Unknown Location");
        if (location != null) {
            LocationTag supportsLogin = locationService.getLocationTagByName(AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN);
            location.removeTag(supportsLogin);
            locationService.saveLocation(location);
        }
    }

    public void deployMetadataPackages(MetadataDeployService service) {
        MetadataBundle ebola = Context.getRegisteredComponent("ebolaMetadata", MetadataBundle.class);
        MetadataBundle ebolaDemoData = Context.getRegisteredComponent("ebolaDemoData", MetadataBundle.class);
        service.installBundles(Arrays.asList(ebola, ebolaDemoData));
    }

    public void setupEmrApiGlobalProperties(AdministrationService administrationService) {
        setGlobalProperty(administrationService, EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE, EbolaMetadata._EncounterRole.CLINICIAN);
    }

    void disableApps(AppFrameworkService service) {
        service.disableApp("coreapps.configuremetadata");
        service.disableApp("coreapps.findPatient");
        service.disableApp("coreapps.activeVisits");
    }

    private void setGlobalProperty(AdministrationService administrationService, String propertyName, String propertyValue) {
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty(propertyName, propertyValue);
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
    }

}
