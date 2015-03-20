package org.openmrs.module.ebolaexample.rest.controller;

import junit.framework.Assert;
import org.apache.commons.lang.time.DateUtils;
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
import java.util.LinkedHashMap;

public class VitalsAndSymptomsObservationControllerTest extends EbolaRestTestBase {

    private String requestURI = "/rest/" + RestConstants.VERSION_1 + "/ebola/vitals-and-symptoms-obs";

    @Test
    public void shouldGetEmptyListIfEncounterNotExisted() throws Exception{
        Patient patient = Context.getPatientService().getPatient(2);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());
        request.addParameter("formUuid", EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS);

        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<LinkedHashMap> obs = (ArrayList<LinkedHashMap>)responseObject.get("obs");

        Assert.assertEquals(0, obs.size());
    }
    @Test
    public void testGetLatest() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);

        Date yesterday = DateUtils.addDays(new Date(), -1);
        HashSet<Obs> obses = new HashSet<Obs>();
        obses.add(getNumericObs(patient, yesterday));
        createEncounter(patient, EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS, yesterday, obses);

        Date today = new Date();
        HashSet<Obs> obses1 = new HashSet<Obs>();
        obses1.add(getNumericObs(patient, today));
        obses1.add(getNumericObs(patient, today));
        createEncounter(patient, EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS, today, obses1);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());
        request.addParameter("formUuid", EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS);

        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<LinkedHashMap> obs = (ArrayList<LinkedHashMap>)responseObject.get("obs");

        Assert.assertEquals(2, obs.size());
    }

    @Test
    public void shouldOnlyGetSymptomsObs() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);

        Date yesterday = DateUtils.addDays(new Date(), -1);
        HashSet<Obs> obses = new HashSet<Obs>();
        obses.add(getNumericObs(patient, yesterday));
        createEncounter(patient, EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS, yesterday, obses);

        Date today = new Date();
        HashSet<Obs> obses1 = new HashSet<Obs>();
        obses1.add(getNumericObs(patient, today));
        obses1.add(getCodedObs(patient, today));
        createEncounter(patient, EbolaMetadata._Form.EBOLA_VITALS_FORM, today, obses1);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());
        request.addParameter("formUuid", EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS);
        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<LinkedHashMap> obs = (ArrayList<LinkedHashMap>)responseObject.get("obs");

        Assert.assertEquals(1, obs.size());
    }

    @Test
    public void shouldGetResultForObsWithNumericAndCodedConcepts() throws Exception{
        Patient patient = Context.getPatientService().getPatient(2);

        Date today = new Date();
        HashSet<Obs> obses1 = new HashSet<Obs>();
        Obs numericObs = getNumericObs(patient, today);
        obses1.add(numericObs);
        Obs codedObs = getCodedObs(patient, today);
        obses1.add(codedObs);
        createEncounter(patient, EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS, today, obses1);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestURI);
        request.addHeader("content-type", "application/json");
        request.addParameter("patientUuid", patient.getUuid());
        request.addParameter("formUuid", EbolaMetadata._Form.EBOLA_CLINICAL_SIGNS_AND_SYMPTOMS);

        MockHttpServletResponse response = webMethods.handle(request);
        SimpleObject responseObject = new ObjectMapper().readValue(response.getContentAsString(), SimpleObject.class);
        ArrayList<LinkedHashMap> obs = (ArrayList<LinkedHashMap>)responseObject.get("obs");

        Assert.assertEquals(2, obs.size());

        for(LinkedHashMap map : obs){
            String concept = (String)map.get("concept");
            if(!concept.equals(numericObs.getConcept().getUuid())){
                Assert.assertEquals(codedObs.getValueCoded().getUuid(), map.get("value"));
            }
            else{
                Assert.assertEquals(numericObs.getValueNumeric(), map.get("value"));
            }
        }
    }

    private void createEncounter(Patient patient, String formUuid, Date dateCreated, HashSet<Obs> obs) {
        EncounterService encounterService = Context.getEncounterService();
        Visit visit = Context.getVisitService().getActiveVisitsByPatient(patient).get(0);
        Provider provider = Context.getProviderService().getAllProviders().get(0);
        EncounterType encounterType = encounterService.getEncounterTypeByUuid(EbolaMetadata._EncounterType.EBOLA_INPATIENT_FOLLOWUP);
        EncounterRole encounterRole = MetadataUtils.existing(EncounterRole.class, EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID);
        Form form = Context.getFormService().getFormByUuid(formUuid);
        Encounter encounter = new Encounter();
        encounter.setForm(form);
        encounter.setPatient(patient);
        encounter.setVisit(visit);
        encounter.setEncounterType(encounterType);
        encounter.setProvider(encounterRole, provider);
        encounter.setEncounterDatetime(dateCreated);
        encounter.setObs(obs);

        encounterService.saveEncounter(encounter);
    }

    private Obs getCodedObs(Patient patient, Date dateCreated) {
        Concept concept = Context.getConceptService().getConcept(21);
        Obs obs = new Obs(patient, concept, dateCreated, null);
        Concept answer_concept = Context.getConceptService().getConcept(7);
        obs.setValueCoded(answer_concept);
        return obs;
    }

    private Obs getNumericObs(Patient patient, Date dateCreated) {
        Concept concept = Context.getConceptService().getConcept(5089);
        Obs obs = new Obs(patient, concept, dateCreated, null);
        obs.setValueNumeric(1.0);
        return obs;
    }
}