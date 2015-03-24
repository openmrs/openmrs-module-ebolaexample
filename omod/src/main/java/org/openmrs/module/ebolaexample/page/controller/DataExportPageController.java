package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.module.ebolaexample.reporting.DataExport;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DataExportPageController {

    public String get(@SpringBean ReportDefinitionService reportDefinitionService, @SpringBean PatientDataService patientDataService,
                      UiUtils ui, HttpServletResponse response) throws IOException, EvaluationException {

        DataExport dataExport = new DataExport();

        ReportDefinition reportDefinition = dataExport.buildFullDataExport();
        ReportData reportData = reportDefinitionService.evaluate(reportDefinition, new EvaluationContext());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"dataExport.csv\"");
        ServletOutputStream outputStream = response.getOutputStream();

        CsvReportRenderer csvReportRenderer = new CsvReportRenderer();
        csvReportRenderer.render(reportData, "", outputStream);
        outputStream.close();

        return "redirect:" + ui.pageLink("referenceapplication", "home", null);
    }

}
