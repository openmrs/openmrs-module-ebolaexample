package org.openmrs.module.ebolaexample.rest.controller;

import junit.framework.Assert;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.EbolaRestTestBase;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class VitalsAndSymptomsObservationControllerTest extends EbolaRestTestBase {

    private String requestURI = "/rest/" + RestConstants.VERSION_1 + "/ebola/vitals-and-symptoms-obs";

    @Test
    public void testGetLatest() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);

        createEncounter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());
        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<Obs> obs = (ArrayList<Obs>)responseObject.get("obs");

        Assert.assertEquals(2, obs.size());
    }

    private void createEncounter() {
        Patient patient = Context.getPatientService().getPatient(2);
        EncounterService encounterService = Context.getEncounterService();
        Visit visit = Context.getVisitService().getActiveVisitsByPatient(patient).get(0);
        Provider provider = Context.getProviderService().getAllProviders().get(0);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);
        EncounterRole encounterRole = MetadataUtils.existing(EncounterRole.class, EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        Concept concept = Context.getConceptService().getConcept(5089);

        Encounter encounter = new Encounter();
        encounter.setPatient(patient);
        encounter.setVisit(visit);
        encounter.setEncounterType(encounterType);
        encounter.setProvider(encounterRole, provider);
        encounter.setEncounterDatetime(new Date());
        HashSet<Obs> obs = new HashSet<Obs>();
        Obs obs1 = new Obs(patient, concept, new Date(), null);
        obs1.setValueNumeric(1.0);
        Obs obs2 = new Obs(patient, concept, new Date(), null);
        obs2.setValueNumeric(2.0);
        obs.add(obs1);
        obs.add(obs2);
        encounter.setObs(obs);

        encounterService.saveEncounter(encounter);
    }
}