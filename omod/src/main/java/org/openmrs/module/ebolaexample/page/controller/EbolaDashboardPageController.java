package org.openmrs.module.ebolaexample.page.controller;

import org.joda.time.DateMidnight;
import org.openmrs.Program;
import org.openmrs.module.ebolaexample.metadata.EbolaMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.InProgramCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.ProgramEnrollmentCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

public class EbolaDashboardPageController {

    public void get(@SpringBean CohortDefinitionService cohortDefinitionService,
                    PageModel model) throws EvaluationException {
        Program program = MetadataUtils.existing(Program.class, EbolaMetadata._Program.EBOLA_PROGRAM);

        // TODO move these definitions to a reporting DefinitionLibrary
        InProgramCohortDefinition inProgramDef = new InProgramCohortDefinition();
        inProgramDef.addProgram(program);

        ProgramEnrollmentCohortDefinition enrolledTodayDef = new ProgramEnrollmentCohortDefinition();
        enrolledTodayDef.addProgram(program);
        enrolledTodayDef.setEnrolledOnOrAfter(new DateMidnight().toDate());

        EvaluationContext evaluationContext = new EvaluationContext();

        EvaluatedCohort inProgram = cohortDefinitionService.evaluate(inProgramDef, evaluationContext);
        EvaluatedCohort enrolledToday = cohortDefinitionService.evaluate(enrolledTodayDef, evaluationContext);

        model.addAttribute("inProgram", inProgram);
        model.addAttribute("enrolledToday", enrolledToday);
    }

}
