package org.openmrs.module.ebolaexample.rest.controller;

import junit.framework.Assert;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.EbolaRestTestBase;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class LaboratoryEncounterControllerTest  extends EbolaRestTestBase {

    private String requestURI = "/rest/" + RestConstants.VERSION_1 + "/ebola/encounter/laboratory";

    @Test
    public void shouldGetEmptyListIfNoEncounter() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());

        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<LinkedHashMap> encounters = (ArrayList<LinkedHashMap>)responseObject.get("encounters");
        Assert.assertEquals(0, encounters.size());
    }

    @Test
    public void shouldGetAllIfTopNotGiven() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);

        Date threeDaysAgo = DateUtils.addDays(new Date(), -3);
        Date twoDaysAgo = DateUtils.addDays(new Date(), -2);
        Date yesterday = DateUtils.addDays(new Date(), -1);
        Date today = DateUtils.addDays(new Date(), 0);

        createEncounter(patient, threeDaysAgo, true, false);
        createEncounter(patient, twoDaysAgo, true, true);
        createEncounter(patient, yesterday, true, true);
        createEncounter(patient, today, false, true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());

        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<LinkedHashMap> encounters = (ArrayList<LinkedHashMap>)responseObject.get("encounters");

        Assert.assertEquals(2, encounters.size());
        Assert.assertEquals(3, ((ArrayList) encounters.get(0).get("value")).size());
        Assert.assertEquals(3, ((ArrayList) encounters.get(1).get("value")).size());
    }

    @Test
    public void shouldGetTopNWhenTopGiven() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);

        Date threeDaysAgo = DateUtils.addDays(new Date(), -3);
        Date twoDaysAgo = DateUtils.addDays(new Date(), -2);
        Date yesterday = DateUtils.addDays(new Date(), -1);
        Date today = DateUtils.addDays(new Date(), 0);

        createEncounter(patient, threeDaysAgo, true, false);
        createEncounter(patient, twoDaysAgo, true, false);
        createEncounter(patient, yesterday, true, false);
        createEncounter(patient, today, false, false);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());
        request.addParameter("top", "2");

        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<LinkedHashMap> encounters = (ArrayList<LinkedHashMap>)responseObject.get("encounters");

        Assert.assertEquals(1, encounters.size());

        ArrayList obsArray = (ArrayList) encounters.get(0).get("value");
        Assert.assertEquals(2, obsArray.size());

        LinkedHashMap obs = (LinkedHashMap)obsArray.get(0);
        Assert.assertEquals(yesterday.getTime()/1000, (Long)obs.get("encounterDatetime")/1000);
        Assert.assertEquals("703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obs.get("value"));

        obs = (LinkedHashMap)obsArray.get(1);
        Assert.assertEquals(twoDaysAgo.getTime()/1000, (Long)obs.get("encounterDatetime")/1000);
        Assert.assertEquals("703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obs.get("value"));
    }

    private void createEncounter(Patient patient, Date dateCreated, boolean hasMalaria, boolean hasEbola) {
        HashSet<Obs> obses = new HashSet<Obs>();
        if(hasMalaria)
        {
            Obs codedObs = getMalariaCodedObs(patient, dateCreated);
            obses.add(codedObs);

        }
        if(hasEbola)
        {
            Obs codedObs = getEbolaCodedObs(patient, dateCreated);
            obses.add(codedObs);
        }
        new EbolaEncounterBuilder().createEncounter(patient, EbolaMetadata._Form.EBOLA_LAB_FORM, dateCreated, obses, EbolaMetadata._EncounterType.EBOLA_LAB_TEST);
    }

    private Obs getMalariaCodedObs(Patient patient, Date dateCreated) {
        Concept concept = Context.getConceptService().getConceptByUuid("32AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Obs obs = new Obs(patient, concept, dateCreated, null);
        Concept answer_concept = Context.getConceptService().getConceptByUuid("703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValueCoded(answer_concept);
        return obs;
    }

    private Obs getEbolaCodedObs(Patient patient, Date dateCreated) {
        Concept concept = Context.getConceptService().getConceptByUuid("1030AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Obs obs = new Obs(patient, concept, dateCreated, null);
        Concept answer_concept = Context.getConceptService().getConceptByUuid("703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValueCoded(answer_concept);
        return obs;
    }
}