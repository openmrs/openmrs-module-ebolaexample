<%
    ui.includeCss("ebolaexample", "overview/ebolaOverview.css")
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        <% if (wardAndBed && wardAndBed.ward) { %>
        {
            label: "${ ui.format(wardAndBed.ward) }",
            link: '${ ui.escapeJs(ui.pageLink("ebolaexample", "findPatientByWard", [ ward: wardAndBed.ward.uuid ])) }'
        },
        <% } %>
        {
            label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }",
            link: '${ ui.pageLink("ebolaexample", "ebolaOverview", [ patient: patient.patient.uuid ]) }'
        },
        {label: "Med Administration"}
    ]
    var patient = {id: ${ patient.id }};
</script>

<style type="text/css">
.group th {
    text-align: left;
}

</style>

${ui.includeFragment("ebolaexample", "overview/patientHeader", [patient: patient.patient, activeVisit: activeVisit, appContextModel: appContextModel])}

<div class="clear"></div>

<a href="${ui.pageLink("ebolaexample", "ebolaOverview", [patient: patient.patient.uuid])}">Back to Summary</a>

<div class="container">
    <div class="dashboard clear">
        <div class="long-info-container column">
            <div class="long-info-section">
                <div class="info-header">
                    <i class="icon-medkit"></i>
                    <h3>Med Administration</h3>
                    <a style="float: right"  href="${ui.pageLink("ebolaexample", "doseManagement", [patient: patient.patient.uuid])}">Modify Data</a>
                </div>
                <div class="info-body">
                    <table>

                    </table>
                </div>
            </div>
        </div>
    </div>
</div>