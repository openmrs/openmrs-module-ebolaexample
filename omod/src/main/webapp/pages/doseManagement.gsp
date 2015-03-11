<%
    def dateAndTimeFormat = new java.text.SimpleDateFormat("d.MMM HH:mm")
    def dateFormat = new java.text.SimpleDateFormat("d.MMM")
    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

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

    def dosesByOrder = doseHistory.dosesByOrder
%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        <% if (wardAndBed && wardAndBed.ward) { %>
        {
            label: "${ ui.format(wardAndBed.ward) }",
            link: '${ ui.escapeJs(ui.pageLink("ebolaexample", "activePatients", [ ward: wardAndBed.ward.uuid ])) }'
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

tr.deleted td {
    text-decoration: line-through;
}

tr td.actions {
    text-align: right;
    text-decoration: none;
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
                </div>

                <div class="info-body">
                    <table>
                        <% if (doseHistory.orders.size() == 0) { %>
                        <tbody>
                        <tr>
                            <td>No prescriptions</td>
                        </tr>
                        </tbody>
                        <% } else { %>
                        <tr>
                            <th colspan="3">Prescription</th>
                            <th class="actions">Actions</th>
                        </tr>
                        <% doseHistory.orders.each { order ->
                            def doses = dosesByOrder[order] ?: []
                        %>
                        <tbody>
                        <tr class="group">
                            <td colspan="3">
                                <strong>
                                    ${ui.format(order.drug)}
                                </strong>
                                <em>
                                    ${formatter.formatPrescription(order, context.locale, ui)}

                                    from
                                    ${dateAndTimeFormat.format(order.dateActivated)}
                                    <% if (order.dateStopped) { %>
                                        stopped
                                        ${dateAndTimeFormat.format(order.dateStopped)}
                                    <% } else if (order.autoExpireDate) { %>
                                        until ${dateAndTimeFormat.format(order.autoExpireDate)}
                                    <% } %>
                                </em>
                            </td>
                            <td class="actions">
                                <a href="${ui.pageLink("ebolaexample", "addEditDose", [patient: patient.patient.uuid, prescription: order.uuid])}">
                                    Add
                                </a>
                            </td>
                        </tr>
                        <% if (doses.size() == 0) { %>
                            <tr>
                                <td colspan="3">No doses</td>
                                <td></td>
                            </tr>
                        <% } %>
                        <% doses.each { dose -> %>
                            <tr <% if (dose.voided) { %>class="deleted"<% } %>>
                                <td>
                                    ${dateFormat.format(dose.scheduledDatetime)}
                                </td>
                                <td>
                                    ${timeFormat.format(dose.scheduledDatetime)}
                                </td>
                                <td>
                                    ${formatStatus(dose.status)}
                                    <% if (dose.reasonNotAdministeredNonCoded) { %>
                                        (${dose.reasonNotAdministeredNonCoded})
                                    <% } %>
                                    <% if (dose.voided) { %>
                                        [ Deleted ]
                                    <% } %>
                                </td>
                                <td class="actions">
                                    <% if (dose.voided) { %>
                                    <a href="${ui.actionLink("ebolaexample", "overview/doseManagement", "restore", [scheduledDoseUuid: dose.uuid])}">
                                        Restore
                                    </a>
                                    <% } else { %>
                                    <a href="" style="display: none">
                                        Edit
                                    </a>
                                    <a href="${ui.actionLink("ebolaexample", "overview/doseManagement", "delete", [scheduledDoseUuid: dose.uuid])}">
                                        Delete
                                    </a>
                                    <% } %>
                                </td>
                            </tr>
                        <% } %>
                        </tbody>
                        <% } %>
                        <% } %>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>