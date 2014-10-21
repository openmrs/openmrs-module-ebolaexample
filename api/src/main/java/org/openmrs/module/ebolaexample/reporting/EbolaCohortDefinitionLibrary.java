package org.openmrs.module.ebolaexample.reporting;

import org.joda.time.DateMidnight;
import org.openmrs.Program;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.stereotype.Component;

@Component
public class EbolaCohortDefinitionLibrary extends BaseDefinitionLibrary<CohortDefinition> {

    public static final java.lang.String PREFIX = "ebolaexample.cohort.";

    public static final String IN_PROGRAM_NOW = "inProgramNow";
    public static final String ENROLLED_TODAY = "enrolledInProgramToday";

    @Override
    public Class<? super CohortDefinition> getDefinitionType() {
        return CohortDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return PREFIX;
    }

    @DocumentedDefinition(IN_PROGRAM_NOW)
    public InProgramCohortDefinition getInProgramNow() {
        Program program = MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM);
        InProgramCohortDefinition inProgramDef = new InProgramCohortDefinition();
        inProgramDef.addProgram(program);
        return inProgramDef;
    }

    @DocumentedDefinition(ENROLLED_TODAY)
    public ProgramEnrollmentCohortDefinition getEnrolledToday() {
        Program program = MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM);
        ProgramEnrollmentCohortDefinition enrolledTodayDef = new ProgramEnrollmentCohortDefinition();
        enrolledTodayDef.addProgram(program);
        enrolledTodayDef.setEnrolledOnOrAfter(new DateMidnight().toDate());
        return enrolledTodayDef;
    }

}
