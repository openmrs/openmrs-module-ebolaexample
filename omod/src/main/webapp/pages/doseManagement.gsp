<%
    def timeFormat = new java.text.SimpleDateFormat("d MMM yyyy HH:mm")

    def DateUtil = context.loadClass("org.openmrs.module.reporting.common.DateUtil")
    def today = DateUtil.getStartOfDay(new Date())

    def formatStatus = {
        def ret = it.name()
        if (ret == 'FULL') {
            return "Fully Given"
        } else if (ret == 'PARTIAL') {
            return "Partially Given"
        } else if (ret == 'NOT_GIVEN') {
            return "Not Given"
        }
        return ret;
    }

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
                    <a style="float: right"
                       href="${ui.pageLink("ebolaexample", "doseManagement", [patient: patient.patient.uuid])}">Modify Data</a>
                </div>

                <div class="info-body">
                    <table>
                        <% if (doseHistory.orders.size() == 0) { %>
                        <tbody>
                        <tr>
                            <td>No prescriptions during this time period</td>
                        </tr>
                        </tbody>
                        <% } %>
                        <tr>
                            <th>Prescription</th>
                            <th>Modify Data</th>
                        </tr>
                        <% doseHistory.ordersGroupedByDrug.each { group -> %>
                        <tbody>
                        <tr class="group">
                            <td>
                                <strong>
                                    ${ui.format(group.key.concept)}
                                </strong>
                                <em>
                                    ${ui.format(group.key)}
                                </em>
                            </td>
                            <td>
                                <a href="">Add</a>
                            </td>
                        </tr>
                        <% group.key.each { drug -> %>
                        <% if (doseHistory.getDosesForDrug(drug).size() == 0) { %>
                        <tr>
                            <td>No doses</td>
                            <td></td>
                        </tr>
                        <% } %>
                        <% doseHistory.getDosesForDrug(drug).each { dose -> %>
                        <tr>
                            <td>
                                ${timeFormat.format(dose.scheduledDatetime)}
                                ${formatStatus(dose.status)}
                            </td>
                            <td>
                                <a href="${ ui.actionLink("ebolaexample", "overview/doseManagement", "delete", [ scheduledDoseId: dose.scheduledDoseId, dateVoided: today, voidedBy: context.authenticatedUser.id]) }">Delete</a>
                                <a href="">Edit</a>
                            </td>
                        </tr>
                        <% } %>
                        <% } %>
                        </tbody>
                        <% } %>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>