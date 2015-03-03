<%
    def EmrApiConstants = context.loadClass("org.openmrs.module.emrapi.EmrApiConstants")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.js")
    ui.includeJavascript("uicommons", "services/locationService.js")

    ui.includeJavascript("ebolaexample", "tabletapp/resources.js")
    ui.includeJavascript("ebolaexample", "overview/inpatientLocation.js")

    ui.includeCss("ebolaexample", "overview/inpatientLocation.css")
%>
<div class="long-info-section">

    <div class="info-header">
        <i class="icon-hospital"></i>

        <h3>Location & Discharge</h3>
    </div>

    <div class="info-body" style="overflow: auto;">

        <% if (currentOutcome) { %>
        <div style="float: left; width: 50%;">
            Patient out of ETC!!
        </div>

        <% } else { %>

        <div style="float: left; width: 50%;" ng-app="inpatientLocation" ng-controller="InpatientLocationCtrl"
             ng-init="init({patientUuid:'${patient.patient.uuid}',
            currentWard: <% if (currentWard) { %>{display:'${currentWard}', uuid:'${currentWard.uuid}'}<%
                 } else { %>null<% } %>,
            currentBed: <% if (currentBed) { %>{display:'${currentBed}', uuid:'${currentBed.uuid}'}<% } else { %>null<%
                     } %>
            })">

            <% if (!config.activeVisit) { %>

            <em>No active visit.</em> <br/>

            <a class="button"
               href="${ui.actionLink("ebolaexample", "overview/inpatientLocation", "startOutpatientVisit",
                       [patient: patient.patient.uuid])}">
                <i class="icon-exchange"></i>
                Outpatient visit
            </a>

            <% } else if (!currentWard) { %>

            <p class="current-ward"><em>Not yet admitted</em></p>

            <em>(To Do: Triage form should automatically admit.)</em>

            <form method="POST"
                  action="${
                          ui.actionLink("ebolaexample", "overview/inpatientLocation", "admit", [patient: patient.patient.uuid])}">
                ${ui.includeFragment("uicommons", "field/location", [
                        label        : "Admit to",
                        formFieldName: "location",
                        withTag      : EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION
                ])}
                <button type="submit">
                    Admit
                </button>
            </form>

            <% } else { %>

            <p class="current-ward">${ui.format(currentWard)},
                <span class="current-bed">${currentBed ? ui.format(currentBed) : "(No Bed)"}</span>

        </p>

            <a class="button" ng-click="changeLocationPatient()">Change</a>

            <% } %>
        </div>

        <% } %>

        <div style="float:right; width: 50%;">
            <span style="display: block;"><strong>Ebola Treatment Outcome:</strong>
                <em>
                    <% if (currentOutcome) { %>
                    ${ui.format(currentOutcome)}, ${patientProgram.dateCompleted.format('dd MMM yyyy, HH:mm')}
                    <% } else { %>None<% } %>
                </em>
            </span>

            <% if (!currentOutcome) { %>
            <a class="button" href="${ui.pageLink("ebolaexample", "changePatientDischarge",
                    [patientUuid: patient.patient.uuid])}">Discharge/Deceased</a>
            <% } %>
        </div>

        <div style="clear: both;"></div>
    </div>
</div>

<script type="text/javascript">
    angular.bootstrap("")
</script>
