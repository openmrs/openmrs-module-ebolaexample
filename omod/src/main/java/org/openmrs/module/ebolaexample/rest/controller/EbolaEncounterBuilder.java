package org.openmrs.module.ebolaexample.rest.controller;

import org.openmrs.*;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;

import java.util.Date;
import java.util.HashSet;

public class EbolaEncounterBuilder {
    void createEncounter(Patient patient, String formUuid, Date dateCreated, HashSet<Obs> obs) {
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
}
