package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.ebolaexample.reporting.EbolaCohortDefinitionLibrary;
import org.openmrs.module.ebolaexample.reporting.EbolaPatientDataDefinitionLibrary;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.library.BuiltInPatientDataLibrary;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.ebolaexample.reporting.EbolaCohortDefinitionLibrary.IN_PROGRAM_NOW;
import static org.openmrs.module.ebolaexample.reporting.EbolaPatientDataDefinitionLibrary.INPATIENT_LOCATION;

public class PatientsByLocationPageController {

    public void get(@SpringBean AllDefinitionLibraries libraries,
                    @SpringBean DataSetDefinitionService dsdService,
                    @SpringBean("locationService") LocationService locationService,
                    PageModel model) throws EvaluationException {
        // TODO limit to those with an active visit
        CohortDefinition enrolled = libraries.getDefinition(CohortDefinition.class, EbolaCohortDefinitionLibrary.PREFIX + IN_PROGRAM_NOW);
        PatientDataDefinition locationDef = libraries.getDefinition(PatientDataDefinition.class, EbolaPatientDataDefinitionLibrary.PREFIX + INPATIENT_LOCATION);
        PatientDataDefinition identifier = libraries.getDefinition(PatientDataDefinition.class, BuiltInPatientDataLibrary.PREFIX + "preferredIdentifier.identifier");
        PatientDataDefinition familyName = libraries.getDefinition(PatientDataDefinition.class, BuiltInPatientDataLibrary.PREFIX + "preferredName.familyName");
        PatientDataDefinition givenName = libraries.getDefinition(PatientDataDefinition.class, BuiltInPatientDataLibrary.PREFIX + "preferredName.givenName");
        PatientDataDefinition birthdate = libraries.getDefinition(PatientDataDefinition.class, BuiltInPatientDataLibrary.PREFIX + "birthdate");
        PatientDataDefinition gender = libraries.getDefinition(PatientDataDefinition.class, BuiltInPatientDataLibrary.PREFIX + "gender");

        PatientDataSetDefinition dsd = new PatientDataSetDefinition();
        dsd.addRowFilter(enrolled, "");
        dsd.addColumn("identifier", identifier, "");
        dsd.addColumn("familyName", familyName, "");
        dsd.addColumn("givenName", givenName, "");
        dsd.addColumn("birthdate", birthdate, "");
        dsd.addColumn("gender", gender, "");
        dsd.addColumn("location", locationDef, "");

        DataSet data = dsdService.evaluate(dsd, new EvaluationContext());

        Map<Location, List<DataSetRow>> byLocation = new HashMap<Location, List<DataSetRow>>();
        for (DataSetRow row : data) {
            Location location = (Location) row.getColumnValue("location");
            List<DataSetRow> rows = byLocation.get(location);
            if (rows == null) {
                rows = new ArrayList<DataSetRow>();
                byLocation.put(location, rows);
            }
            rows.add(row);
        }

        model.addAttribute("byLocation", byLocation);

        LocationTag observationAreaTag = MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_OBSERVATION_AREA);
        LocationTag hotZoneAreaTag = MetadataUtils.existing(LocationTag.class, EbolaMetadata._LocationTag.EBOLA_HOT_ZONE_AREA);
        model.addAttribute("observationAreas", locationService.getLocationsByTag(observationAreaTag));
        model.addAttribute("hotZoneAreas", locationService.getLocationsByTag(hotZoneAreaTag));
    }

}
