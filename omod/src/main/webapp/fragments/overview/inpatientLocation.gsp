<%
    def EmrApiConstants = context.loadClass("org.openmrs.module.emrapi.EmrApiConstants")

    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.js")
    ui.includeJavascript("uicommons", "services/locationService.js")

    ui.includeJavascript("ebolaexample", "tabletapp/resources.js")
    ui.includeJavascript("ebolaexample", "overview/inpatientLocation.js")

    ui.includeCss("ebolaexample", "overview/inpatientLocation.css")
%>


    <span style="width: 50%">
        <div>
            <strong>Ebola stage:</strong>
            <span>
                <% if (config.ebolaStage) { %>
                    ${ui.format(config.ebolaStage)}
                <% } else if (config.ebolaStageAtAdmission) { %>
                    ${ui.format(config.ebolaStageAtAdmission)}
                <% } else { %>
                --
                <% } %>

            </span>
        </div>
        <div>

        <strong>Location:</strong>
            <% if (currentOutcome) { %>
            <span >
                Out of ETC
            </span>

        <% } else { %>

        <span  ng-app="inpatientLocation" ng-controller="InpatientLocationCtrl"
             ng-init="init({patientUuid:'${patient.patient.uuid}',
            currentWard: <% if (currentWard) { %>{display:'${currentWard}', uuid:'${currentWard.uuid}'}<%
                 } else { %>null<% } %>,
            currentBed: <% if (currentBed) { %>{display:'${currentBed}', uuid:'${currentBed.uuid}'}<% } else { %>null<%
                     } %>
            })">

            <% if (!config.activeVisit) { %>

            <em>No active visit.</em> <br/>

            <a class=""
               href="${ui.actionLink("ebolaexample", "overview/inpatientLocation", "startOutpatientVisit",
                       [patient: patient.patient.uuid])}">
                <i class="icon-exchange"></i>
                Outpatient visit
            </a>

            <% } else if (!currentWard) { %>

            --
            <% } else { %>

            <p class="current-ward">${ui.format(currentWard)},
                <span class="current-bed">${currentBed ? ui.format(currentBed) : "(No Bed)"}</span>

            </p>

            <a class="" ng-click="changeLocationPatient()">Change</a>

            <% } %>
        </span>

        <% } %>
        </div>

        <div style="">
            <span style=""><strong>Ebola Treatment Outcome:</strong>
                <em>
                    <% if (currentOutcome) { %>
                    ${ui.format(currentOutcome)}, ${patientProgram.dateCompleted.format('dd MMM yyyy, HH:mm')}
                    <% } else { %>None<% } %>
                </em>
            </span>

            <% if (!currentOutcome) { %>
            <a class="" href="${ui.pageLink("ebolaexample", "changePatientDischarge",
                    [patientUuid: patient.patient.uuid])}">Discharge/Deceased</a>
            <% } %>
        </div>

        <div style="clear: both;"></div>
    </span>

<script type="text/javascript">
    angular.bootstrap("")
</script>
