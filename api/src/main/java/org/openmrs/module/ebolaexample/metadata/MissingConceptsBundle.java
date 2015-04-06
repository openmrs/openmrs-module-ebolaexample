package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.metadatadeploy.builder.ConceptBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptMapBuilder;
import org.openmrs.module.metadatadeploy.builder.ConceptNameBuilder;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Use this to create concepts that we know will be coming down the pipe from CIEL, but haven't been released yet.
 * Once a released CIEL dictionary includes these concepts, we can remove them from this class
 */
@Component("missingConceptsBundle")
public class MissingConceptsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        ConceptDatatype coded = MetadataUtils.existing(ConceptDatatype.class, ConceptDatatype.CODED_UUID);
        ConceptClass test = MetadataUtils.existing(ConceptClass.class, "8d4907b2-c2cc-11de-8d13-0010c6dffd0f");
        ConceptMapType sameAs = MetadataUtils.existing(ConceptMapType.class, "35543629-7d8c-11e1-909d-c80aa9edcf4e");
        ConceptSource ciel = MetadataUtils.existing(ConceptSource.class, "249b13c8-72fa-4b96-8d3d-b200efed985e");
        Concept indeterminate = MetadataUtils.existing(Concept.class, "1138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept negative = MetadataUtils.existing(Concept.class, "664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Concept positive = MetadataUtils.existing(Concept.class, "703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        String ebolaPcrTestUuid = "162599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        if (MetadataUtils.possible(Concept.class, ebolaPcrTestUuid) == null) {
            ConceptName longName = new ConceptNameBuilder("126373BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                    .name("Ebola virus RNA by polymerase chain reaction")
                    .type(ConceptNameType.FULLY_SPECIFIED)
                    .locale(Locale.ENGLISH)
                    .localePreferred(true).build();

            ConceptName synonym = new ConceptNameBuilder("126372BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB")
                    .name("Ebola virus PCR")
                    .locale(Locale.ENGLISH)
                    .localePreferred(false).build();

            Concept ebolaLabTest = new ConceptBuilder(ebolaPcrTestUuid)
                    .datatype(coded)
                    .conceptClass(test)
                    .name(longName)
                    .name(synonym)
                    .description("5d7643fb-a4d5-485f-82c4-bf41a16113ab", "Ebola virus PCR in any sample", Locale.ENGLISH)
                    .answers(indeterminate, negative, positive)
                    .mapping(new ConceptMapBuilder("beb8d5a8-3a61-42e8-803e-cab0ec60be33")
                            .type(sameAs).ensureTerm(ciel, "162599").build())
                    .build();
            ebolaLabTest.setConceptId(162599);
            install(ebolaLabTest);
        }
    }

}
