<%
    ui.includeJavascript("ebolaexample", "overview/tabletappForDesktop.js")
    ui.includeJavascript("ebolaexample", "tabletapp/formentry/question-builder.js")
    ui.includeJavascript("ebolaexample", "tabletapp/formentry/common-metadata.js")
    ui.includeJavascript("ebolaexample", "tabletapp/formentry/questions.js")
    ui.includeJavascript("ebolaexample", "tabletapp/constants.js")
    ui.includeJavascript("ebolaexample", "tabletapp/filters.js")

    ui.includeJavascript("ebolaexample", "overview/vitalsSummary.js")
    ui.includeCss("ebolaexample", "overview/symptomsSummary.css")

%>

<div id="vitals" ng-show="isFeatureEnabled()" class="long-info-section"  ng-controller="VitalsSummaryController"
ng-init="init({patientUuid:'${patient.patient.uuid}', fullConsciousnessName: true})">

    <div class="info-header" >
        <i class="icon-vitals"></i>

        <h3>Vitals</h3>
        <span>
            <a href="" ng-click="getVitalsEncounters(3)" ng-class="{'disabled': !showAll}">Show last 3 observations</a>
            <a href="" ng-click="getVitalsEncounters()" ng-class="{'disabled': showAll}">View All</a>
        </span>
        ${ui.includeFragment("ebolaexample", "overview/actions", [patient: patient, currentAssignment:wardAndBed])}
    </div>

    <div class="info-body">
        <div ng-if="vitalsEncounters.length == 0">
            ${ui.message("coreapps.none")}
        </div>
        <table >

            <tr ng-repeat="encounter in vitalsEncounters">
                <td width="100px" style="border: none">{{encounter.dateCreated | simpleDate }}</td>
                <td style="border:none" ng-repeat="ob in getObsDesc(encounter.obs)">
                    {{ob}}
                </td>
            </tr>
        </table>
    </div>
    <div>
        <a href="#" class="right back-to-top">Back to top</a>
    </div>
    <br/>


</div>

<script>
    angular.bootstrap("#vitals", ['vitalsSummary']);
</script>
