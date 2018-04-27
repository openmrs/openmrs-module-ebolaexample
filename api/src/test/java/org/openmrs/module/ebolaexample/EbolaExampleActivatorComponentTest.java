package org.openmrs.module.ebolaexample;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.api.MetadataDeployService;
import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;
import org.springframework.beans.factory.annotation.Autowired;

public class EbolaExampleActivatorComponentTest extends EbolaMetadataTest {

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private MetadataDeployService metadataDeployService;

    @Autowired
    private EbolaMetadata ebolaMetadata;

    @Test
    public void testStarted() throws Exception {
        new EbolaExampleActivator().started();
        //Do a check like this if we have any concepts in MissingConceptsBundle
        //assertThat(conceptService.getConceptByUuid("162599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getConceptId(), is(162599));
    }

    @Test
    public void setPreferredConceptName_shouldSetPreferredName() throws Exception {
        metadataDeployService.installBundles(Arrays.<MetadataBundle>asList(ebolaMetadata));
        ConceptNameTag nameTag = MetadataUtils.existing(ConceptNameTag.class, EbolaMetadata._ConceptNameTag.PREFERRED);

        String uuid = "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
        Concept weight = conceptService.getConceptByUuid(uuid);
        weight.addName(new ConceptName("Weight", Locale.ENGLISH));
        conceptService.saveConcept(weight);
        assertThat(conceptService.getConceptByUuid(uuid).getPreferredName(Locale.ENGLISH).getName(), is("WEIGHT (KG)"));

        new EbolaExampleActivator().setPreferredConceptName(conceptService, uuid, "Weight");
        assertThat(conceptService.getConceptByUuid(uuid).getPreferredName(Locale.ENGLISH).getName(), is("Weight"));
        assertThat(conceptService.getConceptByUuid(uuid).findNameTaggedWith(nameTag).getName(), is("Weight"));
    }

    @Test
    public void setPreferredConceptName_shouldTagShortName() throws Exception {
        metadataDeployService.installBundles(Arrays.<MetadataBundle>asList(ebolaMetadata));
        ConceptNameTag nameTag = MetadataUtils.existing(ConceptNameTag.class, EbolaMetadata._ConceptNameTag.PREFERRED);

        String uuid = "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
        assertThat(conceptService.getConceptByUuid(uuid).getPreferredName(Locale.ENGLISH).getName(), is("WEIGHT (KG)"));

        new EbolaExampleActivator().setPreferredConceptName(conceptService, uuid, "WT");
        assertThat(conceptService.getConceptByUuid(uuid).getPreferredName(Locale.ENGLISH).getName(), is("WEIGHT (KG)"));
        assertThat(conceptService.getConceptByUuid(uuid).findNameTaggedWith(nameTag).getName(), is("WT"));
    }

}