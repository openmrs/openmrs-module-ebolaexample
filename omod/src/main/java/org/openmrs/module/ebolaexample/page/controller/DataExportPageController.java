package org.openmrs.module.ebolaexample.page.controller;

import org.openmrs.module.ebolaexample.reporting.DataExport;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.FileDownload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataExportPageController {

    public Object get(@SpringBean ReportDefinitionService reportDefinitionService) throws IOException, EvaluationException {

        DataExport dataExport = new DataExport();

        ReportDefinition reportDefinition = dataExport.buildFullDataExport();
        ReportData reportData = reportDefinitionService.evaluate(reportDefinition, new EvaluationContext());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        CsvReportRenderer csvReportRenderer = new CsvReportRenderer();
        csvReportRenderer.render(reportData, "", outputStream);

        outputStream.close();

        return new FileDownload("data_export.zip", "application/zip", outputStream.toByteArray());
    }
}
