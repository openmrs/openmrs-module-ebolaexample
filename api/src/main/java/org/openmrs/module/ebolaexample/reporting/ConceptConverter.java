package org.openmrs.module.ebolaexample.reporting;

import org.openmrs.Concept;
import org.openmrs.PatientProgram;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.common.ReflectionUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;

import java.util.Locale;

public class ConceptConverter implements DataConverter {
    private final Class<?> typeToConvert;
    private final String propertyName;
    private ObjectFormatter formatter;

    public ConceptConverter(Class<?> typeToConvert, String propertyName) {
        this.formatter = new ObjectFormatter(Locale.ENGLISH);
        this.typeToConvert = typeToConvert;
        this.propertyName = propertyName;
    }

    @Override
    public Object convert(Object o) {
        String propertyName = (String) ObjectUtil.nvl(this.propertyName, "");
        if (o != null) {
            if (ObjectUtil.isNull(propertyName)) {
                return o.toString();
            } else {
                Concept outcome = (Concept) ReflectionUtil.getPropertyValue(o, propertyName);
                return this.formatter.convert(outcome);
            }
        }
        return null;
    }

    @Override
    public Class<?> getInputDataType() {
        return typeToConvert;
    }

    @Override
    public Class<?> getDataType() {
        return Concept.class;
    }
}
