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


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.appframework.AppFrameworkConstants;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.ebolaexample.importer.DrugImporter;
import org.openmrs.module.ebolaexample.importer.ImportNotes;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.metadata.KerryTownMetadata;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.htmlformentry.HtmlFormEntryService;
import org.openmrs.module.htmlformentryui.HtmlFormUtil;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.openmrs.ui.framework.resource.ResourceFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.openmrs.module.ebolaexample.importer.DrugImporter.DRUG_LIST_VERSION;
import static org.openmrs.module.ebolaexample.importer.DrugImporter.KERRY_TOWN_EBOLA_INSTALLED_DRUG_LIST_VERSION;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EbolaExampleActivator extends BaseModuleActivator {

    protected Log log = LogFactory.getLog(getClass());

    private DrugImporter drugImporter;

    private ConceptService conceptService;

    private AdministrationService administrationService;

    @Override
    public void started() {
        try {
            MetadataDeployService metadataDeployService = Context.getService(MetadataDeployService.class);
            administrationService = Context.getAdministrationService();
            FormService formService = Context.getFormService();
            HtmlFormEntryService htmlFormEntryService = Context.getService(HtmlFormEntryService.class);
            LocationService locationService = Context.getLocationService();
            EmrApiProperties emrApiProperties = Context.getRegisteredComponents(EmrApiProperties.class).get(0);

            drugImporter = Context.getRegisteredComponents(DrugImporter.class).get(0);
            conceptService = Context.getConceptService();

            deployMetadataPackages(metadataDeployService);

            setupEmrApiGlobalProperties(administrationService);

            setupHtmlForms(formService, htmlFormEntryService);

            disableApps(Context.getService(AppFrameworkService.class));

            removeTagsFromUnknownLocation(locationService, emrApiProperties);

            setPreferredConceptNames(conceptService);

            // hack to set the SCI-requested address format for Sierra Leone
            GlobalProperty sciAddressTemplate = new GlobalProperty("layout.address.format",
                    "<org.openmrs.layout.web.address.AddressTemplate>"
                            + "<nameMappings class=\"properties\">"
                            + "<property name=\"countyDistrict\" value=\"Location.district\"/>"
                            + "<property name=\"address2\" value=\"Chiefdom\"/>"
                            + "<property name=\"cityVillage\" value=\"Location.cityVillage\"/>"
                            + "<property name=\"address1\" value=\"Address\"/>"
                            + "</nameMappings>"
                            + "<sizeMappings class=\"properties\">"
                            + "<property name=\"countyDistrict\" value=\"20\"/>"
                            + "<property name=\"address2\" value=\"40\"/>"
                            + "<property name=\"cityVillage\" value=\"20\"/>"
                            + "<property name=\"address1\" value=\"40\"/>"
                            + "</sizeMappings>"
                            + "<lineByLineFormat>"
                            + "<string>countyDistrict</string>"
                            + "<string>address2</string>"
                            + "<string>cityVillage</string>"
                            + "<string>address1</string>"
                            + "</lineByLineFormat>"
                            + "</org.openmrs.layout.web.address.AddressTemplate>",
                    "XML description of address formats");

            administrationService.saveGlobalProperty(sciAddressTemplate);

            importDrugs();

            log.info("Started Ebola Example module");
        } catch (Exception ex) {
            Module mod = ModuleFactory.getModuleById("ebolaexample");
            ModuleFactory.stopModule(mod);
            throw new RuntimeException("Failed to set up the ebolaexample module", ex);
        }
    }

    private void setPreferredConceptNames(ConceptService service) {
        // TODO the CIEL dictionary was recently improved to have many of these as preferred names out of the box, so we can remove quite a few of these.
        setPreferredConceptName(service, "160240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Oral");
        setPreferredConceptName(service, "160242AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "IV");
        setPreferredConceptName(service, "160243AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "IM");
        setPreferredConceptName(service, "160245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Subcutaneous");
        setPreferredConceptName(service, "160241AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Inhaled");
        setPreferredConceptName(service, "161253AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Intranasal");
        setPreferredConceptName(service, "162385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "In left ear");
        setPreferredConceptName(service, "162386AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "In right ear");
        setPreferredConceptName(service, "162387AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "In both ears");
        setPreferredConceptName(service, "160245AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Subcutaneous");
        setPreferredConceptName(service, "162388AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "In left eye");
        setPreferredConceptName(service, "162389AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "In right eye");
        setPreferredConceptName(service, "162390AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "In both eyes");
        setPreferredConceptName(service, "162392AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Per vagina");
        setPreferredConceptName(service, "162393AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Per rectum");
        setPreferredConceptName(service, "162798AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Per NG tube");
        setPreferredConceptName(service, "162624AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "IO (intraosseous) needle insertion");

        setPreferredConceptName(service, "161553AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mg");
        setPreferredConceptName(service, "161554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "g");
        setPreferredConceptName(service, "162263AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mL");
        setPreferredConceptName(service, "162761AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "mg/kg");

        setPreferredConceptName(service, "70116AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Paracetamol");
        setPreferredConceptName(service, "103525AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Gaviscon");
        setPreferredConceptName(service, "76464AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "Flucloxacillin");
    }

    // not private so we can test it
    void setPreferredConceptName(ConceptService service, String uuid, String preferredEnglishName) {
        Concept concept = service.getConceptByUuid(uuid);
        if (concept == null) {
            log.warn("Cannot find concept with uuid " + uuid + " (so not setting preferred name to " + preferredEnglishName);
            return;
        }
        ConceptNameTag ebolaPreferredName = MetadataUtils.existing(ConceptNameTag.class, EbolaMetadata._ConceptNameTag.PREFERRED);
        ConceptName taggedName = concept.findNameTaggedWith(ebolaPreferredName);
        boolean alreadyTagged = taggedName != null && taggedName.getName().equals(preferredEnglishName);
        ConceptName old = concept.getPreferredName(Locale.ENGLISH);
        boolean preferredAlreadySet = old != null && old.getName().equals(preferredEnglishName);
        if (alreadyTagged && preferredAlreadySet) {
            return;
        }
        for (ConceptName candidate : concept.getNames(Locale.ENGLISH)) {
            if (candidate.getName().equals(preferredEnglishName)) {
                boolean anyChange = false;
                if (!preferredAlreadySet) {
                    if (!(candidate.isShort() || candidate.isIndexTerm())) {
                        candidate.setLocalePreferred(true);
                        old.setLocalePreferred(false);
                        anyChange = true;
                    }
                }
                if (!alreadyTagged) {
                    if (taggedName != null) {
                        taggedName.removeTag(ebolaPreferredName);
                    }
                    candidate.addTag(ebolaPreferredName);
                    anyChange = true;
                }
                if (anyChange) {
                    service.saveConcept(concept);
                }
                return;
            }
        }

        // if we get here that means we didn't find the wanted name in the loop above
        log.warn("Cannot find a name \"" + preferredEnglishName + "\" on concept " + uuid);

    }

    private void importDrugs() {

        try {
            int installedDrugListVersion = getIntegerByGlobalProperty(administrationService, KERRY_TOWN_EBOLA_INSTALLED_DRUG_LIST_VERSION, -1);

            if (installedDrugListVersion < DRUG_LIST_VERSION) {

                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Kerry_Town_Drugs_v" + DRUG_LIST_VERSION + ".csv");
                InputStreamReader reader = new InputStreamReader(inputStream);

                ImportNotes notes = drugImporter.importSpreadsheet(reader);

                if (notes.hasErrors()) {
                    log.error("Unable to import drug list. Import notes:");
                    log.error(notes.toString());
                    throw new RuntimeException("Unable to install drug list");
                } else {
                    setGlobalProperty(administrationService, KERRY_TOWN_EBOLA_INSTALLED_DRUG_LIST_VERSION, DRUG_LIST_VERSION.toString());
                }
            }
        } catch (Exception e) {
            log.error("XXXXXXXXXXXXXXXX ------------ Import Error ------------- XXXXXXXXXXXXXXXXX");
            log.error("Import Error", e);
        }
    }

    private void setupHtmlForms(FormService formService, HtmlFormEntryService htmlFormEntryService) throws Exception {
        try {
            ResourceFactory resourceFactory = ResourceFactory.getInstance();
            List<String> htmlforms = Arrays.asList("ebolaexample:htmlforms/triage.xml");

            for (String htmlform : htmlforms) {
                HtmlFormUtil.getHtmlFormFromUiResource(resourceFactory, formService, htmlFormEntryService, htmlform);
            }
        } catch (Exception e) {
            // this is a hack to get component test to pass until we find the proper way to mock this
            if (ResourceFactory.getInstance().getResourceProviders() == null) {
                log.error("Unable to load HTML forms--this error is expected when running component tests, but it is an error if you see it in production");
            } else {
                throw e;
            }
        }
    }

    private void removeTagsFromUnknownLocation(LocationService locationService, EmrApiProperties emrApiProperties) {
        Location location = locationService.getLocation("Unknown Location");
        if (location != null) {
            LocationTag supportsLogin = locationService.getLocationTagByName(AppFrameworkConstants.LOCATION_TAG_SUPPORTS_LOGIN);
            location.removeTag(supportsLogin);
            location.removeTag(emrApiProperties.getSupportsAdmissionLocationTag());
            location.removeTag(emrApiProperties.getSupportsTransferLocationTag());
            locationService.saveLocation(location);
        }
    }

    public void deployMetadataPackages(MetadataDeployService service) {
        MetadataBundle ebola = Context.getRegisteredComponent("ebolaMetadata", MetadataBundle.class);
        MetadataBundle kerryTownMetadata = Context.getRegisteredComponent("kerryTownMetadata", MetadataBundle.class);
        MetadataBundle ebolaDemoData = Context.getRegisteredComponent("ebolaDemoData", MetadataBundle.class);
        MetadataBundle ebolaRolePrivilegeMetadata = Context.getRegisteredComponent("ebolaRolePrivilegeMetadata", MetadataBundle.class);
        service.installBundles(Arrays.asList(ebola, kerryTownMetadata, ebolaDemoData, ebolaRolePrivilegeMetadata));
    }

    public void setupEmrApiGlobalProperties(AdministrationService administrationService) {
        setGlobalProperty(administrationService, EmrApiConstants.GP_CLINICIAN_ENCOUNTER_ROLE, EbolaMetadata._EncounterRole.CLINICIAN);
        setGlobalProperty(administrationService, EmrApiConstants.PRIMARY_IDENTIFIER_TYPE, KerryTownMetadata._PatientIdentifierType.KERRY_TOWN_IDENTIFIER);
    }

    void disableApps(AppFrameworkService service) {
        service.disableApp("coreapps.configuremetadata");
        service.disableApp("coreapps.findPatient");
        service.disableApp("coreapps.activeVisits");
        service.disableApp("coreapps.dataManagementApp");
        service.disableApp("referenceapplication.registrationapp.registerPatient");
        service.disableApp("referenceapplication.vitals");
    }

    private void setGlobalProperty(AdministrationService administrationService, String propertyName, String propertyValue) {
        GlobalProperty gp = administrationService.getGlobalPropertyObject(propertyName);
        if (gp == null) {
            gp = new GlobalProperty(propertyName, propertyValue);
        }
        gp.setPropertyValue(propertyValue);
        administrationService.saveGlobalProperty(gp);
    }

    protected Integer getIntegerByGlobalProperty(AdministrationService administrationService, String globalPropertyName, Integer defaultValue) {
        String value = administrationService.getGlobalProperty(globalPropertyName);
        if (StringUtils.isNotEmpty(value)) {
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                throw new IllegalStateException("Global property " + globalPropertyName + " value of " + value + " is not parsable as an Integer");
            }
        } else {
            return defaultValue;
        }
    }

}
