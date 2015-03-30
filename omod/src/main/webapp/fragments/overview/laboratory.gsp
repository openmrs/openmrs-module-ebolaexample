<%
    ui.includeJavascript("ebolaexample", "overview/laboratory.js")

    def laboratoryUrl = ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithStandardUi", [
            patientId: patient.patient.uuid,
            visitId:activeVisit?.visit?.uuid,
            definitionUiResource: "ebolaexample:htmlforms/laboratory.xml",
            returnUrl: ui.thisUrl()
    ])
%>

<div id="laboratory" ng-show="isFeatureEnabled()" class="long-info-section"  ng-controller="LaboratoryController">

    <div class="info-header" >
        <i class="icon-medkit"></i>

        <h3>Laboratory</h3>
        <span>
            <a href="" ng-click="getEncounters(2)" ng-class="{'disabled': !showAll}">Show last 2 observations</a>
            <a href="" ng-click="getEncounters()" ng-class="{'disabled': showAll}">View All</a>
        </span>
        <a href=${laboratoryUrl} class="right">Add lab result</a>
    </div>

    <div class="info-body">
        <div ng-if="encounters.length == 0">
            ${ui.message("coreapps.none")}
        </div>
        <table >
            <tr ng-repeat="encounter in encounters">
            </tr>
        </table>
    </div>
    <br/>
</div>

<script>
    angular.bootstrap("#laboratory", ['laboratory']);
</script>
