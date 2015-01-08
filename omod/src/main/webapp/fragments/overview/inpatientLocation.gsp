<%
    def EmrApiConstants = context.loadClass("org.openmrs.module.emrapi.EmrApiConstants")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-app.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")
    ui.includeJavascript("uicommons", "angular-ui/ui-bootstrap-tpls-0.11.2.js")
    ui.includeJavascript("uicommons", "services/locationService.js")

    ui.includeJavascript("ebolaexample", "overview/inpatientLocation.js")

    ui.includeCss("ebolaexample", "overview/inpatientLocation.css")
%>
<div class="info-section">

    <div class="info-header">
        <i class="icon-hospital"></i>

        <h3>Location</h3>
    </div>

    <div class="info-body" ng-app="inpatientLocation" ng-controller="InpatientLocationCtrl"
         ng-init="init({patientUuid:'${patient.patient.uuid}',
            currentWard: <% if (currentWard) { %>{display:'${currentWard }', uuid:'${currentWard.uuid}'}<% } else { %>null<% } %>,
            currentBed: <% if (currentBed) { %>{display:'${currentBed}', uuid:'${currentBed.uuid}'}<% } else { %>null<% } %>
            })">

        <% if (!config.activeVisit) { %>

        No active visit. <br/>

        <a class="button" href="${ui.actionLink("ebolaexample", "overview/inpatientLocation", "startOutpatientVisit",
                [patient: patient.patient.uuid])}">
            <i class="icon-exchange"></i>
            Outpatient visit
        </a>

        <% } else if (!currentWard) { %>

        <p class="current-ward">Not yet admitted</p>

        <em>(To Do: Triage form should automatically admit.)</em>

        <form method="POST" action="${ui.actionLink("ebolaexample", "overview/inpatientLocation", "admit", [patient: patient.patient.uuid])}">
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

        <button class="small" ng-hide="makingChange" ng-click="makingChange = true">Change</button>

       <div id="making-changes" ng-show="makingChange" >

            <div class="locationFormDiv">
                <h2>Select type of ward </h2>
                <label ng-repeat="wardType in wardTypes"
                       class="button {{changeToWardType.uuid == wardType.uuid ?'assigned':''}}"
                       ng-model="\$parent.changeToWardType" btn-radio="wardType" uncheckable>
                    {{ wardType.display }}
                </label>
            </div>

            <div ng-show="changeToWardType" class="locationFormDiv">

                <h2>Select the ward</h2>

                <label ng-repeat="ward in wardsOfType(changeToWardType)"
                       class="button {{changeToWard.uuid == ward.uuid ?'assigned':''}}"
                       ng-model="\$parent.changeToWard" btn-radio="ward" uncheckable >
                    {{ ward.display }}
                </label>
            </div>


            <div ng-show="changeToWard" class="locationFormDiv">

                <h2>Select the bed</h2>

                <div class="button-group" ng-show="changeToWard">
                    <label ng-repeat="bed in bedsForWard(changeToWard)" class="button {{changeToBed.uuid == bed.uuid ?'assigned':''}}" ng-model="\$parent.changeToBed" btn-radio="bed"
                           uncheckable>
                        {{ bed.display }}
                    </label>
                </div>
            </div>

            <br/>

            <button class="confirm right" ng-disabled="!changeToWard" ng-click="doTransfer()">Save</button>
            <button class="cancel" ng-click="makingChange = false">Cancel</button>
        </div>
        <% } %>
    </div>
</div>

<script type="text/javascript">
    angular.bootstrap("")
</script>
