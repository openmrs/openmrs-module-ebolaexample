package org.openmrs.module.ebolaexample.uiframework;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNameTag;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.ui.framework.Formatter;
import org.openmrs.ui.framework.formatter.FormatterFactory;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ConceptFormatter implements FormatterFactory {

    @Autowired
    private MessageSourceService messageSourceService;

    private MessageSource messageSource;

    @Override
    public String getForClass() {
        return Concept.class.getName();
    }

    @Override
    public Integer getOrder() {
        return 0;
    }

    @Override
    public Formatter createFormatter(final FormatterService service) throws Exception {
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
                String localization = getLocalization(locale, "Concept", ((Concept) o).getUuid());
                if (localization != null) {
                    return localization;
                }
                else {
                    return bestName(tag, locale, (Concept) o);
                }
            }
        };
    }

    public static String bestName(ConceptNameTag tag, Locale locale, Concept concept) {
        ConceptName tagged = tag == null ? null : concept.findNameTaggedWith(tag);
        return tagged != null ? tagged.getName() : concept.getName(locale).getName();
    }

    // mainly copied from FormatterImpl
    private String getLocalization(Locale locale, String shortClassName, String uuid) {
        if (messageSource == null) {
            messageSource = messageSourceService.getActiveMessageSource();
        }
        if (messageSource == null) {
            return null;
        }

        // in case this is a hibernate proxy, strip off anything after an underscore
        // ie: EncounterType_$$_javassist_26 needs to be converted to EncounterType
        int underscoreIndex = shortClassName.indexOf("_$");
        if (underscoreIndex > 0) {
            shortClassName = shortClassName.substring(0, underscoreIndex);
        }

        String code = "ui.i18n." + shortClassName + ".name." + uuid;
        String localization = messageSource.getMessage(code, null, locale);
        if (localization == null || localization.equals(code)) {
            return null;
        } else {
            return localization;
        }
    }
}
