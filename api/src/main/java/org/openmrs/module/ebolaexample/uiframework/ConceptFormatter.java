package org.openmrs.module.ebolaexample.uiframework;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.Formatter;
import org.openmrs.ui.framework.formatter.FormatterFactory;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ConceptFormatter implements FormatterFactory {

    @Override
    public String getForClass() {
        return Concept.class.getName();
    }

    @Override
    public Integer getOrder() {
        return 0;
    }

    @Override
    public Formatter createFormatter(FormatterService service) throws Exception {
        return new Formatter() {

            private ConceptNameTag tag;

            @Override
            public String format(Object o, Locale locale) {
                if (o == null) {
                    return null;
                }
                if (tag == null) {
                    tag = MetadataUtils.existing(ConceptNameTag.class, EbolaMetadata._ConceptNameTag.PREFERRED);
                }
                return bestName(tag, locale, (Concept) o);
            }
        };
    }

    public static String bestName(ConceptNameTag tag, Locale locale, Concept concept) {
        ConceptName tagged = tag == null ? null : concept.findNameTaggedWith(tag);
        return tagged != null ? tagged.getName() : concept.getName(locale).getName();
    }
}
