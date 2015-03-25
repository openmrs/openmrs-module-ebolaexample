<%
    def dateFormat = new java.text.SimpleDateFormat("d.MMM HH:mm")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.js")
    ui.includeJavascript("uicommons", "services/locationService.js")

    ui.includeJavascript("ebolaexample", "tabletapp/resources.js")
    ui.includeJavascript("ebolaexample", "overview/inpatientLocation.js")
%>
<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>Prescriptions</h3>

        <% if (showAll) { %>
        <a class="right" href="${ui.pageLink("ebolaexample", "ebolaOverview", [patient: patient.patient.uuid])}">
            Back to Summary
        </a>
        <% } else { %>
        <a href="${ui.pageLink("ebolaexample", "allPrescriptions", [patient: patient.patient.id])}">
            View All
        </a>
        <% } %>
        <div style="margin-top:1px; margin-right: 10px; float: right;" ng-app="inpatientLocation" ng-controller="InpatientLocationCtrl"
             ng-init="init({patientUuid:'${patient.patient.uuid}',
            currentWard: <% if (currentWard) { %>{display:'${currentWard}', uuid:'${currentWard.uuid}'}<%
                 } else { %>null<% } %>,
            currentBed: <% if (currentBed) { %>{display:'${currentBed}', uuid:'${currentBed.uuid}'}<% } else { %>null<%
                 } %>
            })">
            ${ui.includeFragment("ebolaexample", "overview/actions", [patient: patient, currentAssignment:wardAndBed])}

        </div>
    </div>

    <div class="info-body">
        <% if (groupedOrders.size() > 0) { %>
        <ul>
            <% groupedOrders.each { group ->
                def groupInactive = group.key.flags.contains("inactive");
            %>
            <li class="prescription-group<% if (groupInactive) { %> inactive<% } %>">
                <% if (group.key.flags.contains("recent")) { %>
                <span class="lozenge recent">RECENT</span>
                <% } %>
                <strong>
                    ${ui.format(group.key.concept)}
                </strong>
                <% if (group.key.drug) { %>
                <em>
                    ${ui.format(group.key.drug)}
                </em>

                <% } %>
                <ul>
                    <% group.value.each { %>

                    <li class="clear <% if (!it.active) { %>inactive<% } %>">
                        <span class="right">
                            ${dateFormat.format(it.dateActivated)}
                            <% if (it.dateStopped) { %>
                            -
                            ${dateFormat.format(it.dateStopped)}
                            <% } else if (it.autoExpireDate) { %>
                            -
                            <em>${dateFormat.format(it.autoExpireDate)}</em>
                            <% } %>
                        </span>
                        <% if (!it.active) { %>
                        <span class="lozenge stopped">
                            ${it.dateStopped ? "STOPPED" : "EXPIRED"}
                        </span>
                        <% } %>
                        ${prescriptionFormatter.formatPrescription(it, context.locale, ui)}

                        <span class="prescriber">
                            <% if(it.orderer) { %>
                                <em> (prescribed by: ${it.orderer.getName()})</em>
                            <% } else {%>
                                <em> (prescribed by: Unknown User)</em>
                            <% } %>
                        </span>

                    </li>
                    <% } %>
                </ul>
            </li>
            <% } %>
        </ul>
        <% } else { %>
        ${ui.message("coreapps.none")}
        <% } %>



        <div style="clear:both;"></div>
    </div>

</div>
<div>
    <a href="#" class="right">Back to top</a>
</div>