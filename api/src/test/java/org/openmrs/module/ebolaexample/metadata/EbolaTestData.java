package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EbolaTestData extends AbstractMetadataBundle {

    @Autowired
    ConceptService conceptService;

    @Override
    public void install() throws Exception {
        // We cannot do install(concept(...)) because of: java.lang.UnsupportedOperationException: Concepts can only be fetched for now
        conceptService.saveConcept(concept("Ebola Program", "162637AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "N/A", "Program"));
    }

    private Concept concept(String name, String uuid, String datatype, String conceptClass) {
        Concept concept = new Concept();
        concept.addName(new ConceptName(name, Locale.ENGLISH));
        concept.setUuid(uuid);
        concept.setDatatype(conceptService.getConceptDatatypeByName(datatype));
        concept.setConceptClass(conceptService.getConceptClassByName(conceptClass));
        return concept;
    }

}
