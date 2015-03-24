<%
    ui.includeJavascript("ebolaexample", "overview/tabletappForDesktop.js")
    ui.includeJavascript("ebolaexample", "tabletapp/formentry/question-builder.js")
    ui.includeJavascript("ebolaexample", "tabletapp/formentry/common-metadata.js")
    ui.includeJavascript("ebolaexample", "tabletapp/formentry/questions.js")
    ui.includeJavascript("ebolaexample", "tabletapp/constants.js")
    ui.includeJavascript("ebolaexample", "tabletapp/filters.js")

    ui.includeJavascript("ebolaexample", "overview/symptomsSummary.js")
    ui.includeCss("ebolaexample", "overview/symptomsSummary.css")

%>

<div id="symptoms" ng-show="isFeatureEnabled()" class="long-info-section"  ng-controller="SymptomsSummaryController"
ng-init="init({patientUuid:'${patient.patient.uuid}'})">

    <div class="info-header" >
        <i class="icon-hospital"></i>

        <h3>Symptoms</h3>
        <span style="margin-left: 100px">
            <a href="" ng-click="getSymptomsEncounters(3)" ng-class="{'disabled': !showAll}">Show last 3 observations</a>
            <a href="" ng-click="getSymptomsEncounters()" ng-class="{'disabled': showAll}">View All</a>
        </span>

    </div>

    <div class="info-body">
        <table>
            <tr  ng-repeat="encounter in symptomsEncounters">
                <td >{{encounter.dateCreated | simpleDate }}</td>
                <td >
                    <span ng-repeat="row in getObsDesc(encounter.obs)">
                        {{row}}
                    </span>
                    <span></span>
                </td>
            </tr>
        </table>
        <div style="clear: both;"></div>

    </div>

</div>


<script>
    angular.bootstrap("#symptoms", ['symptomsSummary']);
</script>
