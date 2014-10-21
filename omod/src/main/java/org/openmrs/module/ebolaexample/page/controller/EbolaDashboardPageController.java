package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.definition.library.AllDefinitionLibraries;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;

import static org.openmrs.module.ebolaexample.reporting.EbolaCohortDefinitionLibrary.ENROLLED_TODAY;
import static org.openmrs.module.ebolaexample.reporting.EbolaCohortDefinitionLibrary.IN_PROGRAM_NOW;
import static org.openmrs.module.ebolaexample.reporting.EbolaCohortDefinitionLibrary.PREFIX;

public class EbolaDashboardPageController {

    public void get(@SpringBean CohortDefinitionService cohortDefinitionService,
                    @SpringBean AllDefinitionLibraries definitionLibraries,
                    PageModel model) throws EvaluationException {
        CohortDefinition inProgramDef = definitionLibraries.getDefinition(CohortDefinition.class, PREFIX + IN_PROGRAM_NOW);
        CohortDefinition enrolledTodayDef = definitionLibraries.getDefinition(CohortDefinition.class, PREFIX + ENROLLED_TODAY);

        EvaluationContext evaluationContext = new EvaluationContext();

        EvaluatedCohort inProgram = cohortDefinitionService.evaluate(inProgramDef, evaluationContext);
        EvaluatedCohort enrolledToday = cohortDefinitionService.evaluate(enrolledTodayDef, evaluationContext);

        model.addAttribute("inProgram", inProgram);
        model.addAttribute("enrolledToday", enrolledToday);
    }

}
