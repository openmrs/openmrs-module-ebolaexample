<%
    ui.includeJavascript("uicommons", "handlebars/handlebars.min.js")
    ui.includeCss("ebolaexample", "overview/ebolaOverview.css")
    ui.decorateWith("appui", "standardEmrPage")

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
<div class="info-section">

    <style type="text/css">
        #bed-group {
            display: flex;
            flex-flow: column wrap;
            height: 225px;
        }
    </style>

    <a href="${ ui.pageLink("ebolaexample", "ebolaOverview", [ patient: patient.uuid ]) }">
        &larr; Back to Summary (discard changes)
    </a>

    <h2>
        ${ ui.format(patient) }
    </h2>

    <div class="info-body" ng-app="inpatientLocation" ng-controller="InpatientLocationCtrl"
         ng-init="init({patientUuid:'${patient.uuid}',
            currentWard: <% if (currentWard) { %>{display:'${currentWard}', uuid:'${currentWard.uuid}'}<% } else { %>null<% } %>,
            currentBed: <% if (currentBed) { %>{display:'${currentBed}', uuid:'${currentBed.uuid}'}<% } else { %>null<% } %>
            })">

        <% if (!activeVisit) { %>

        No active visit. <br/>

        <a class="button" href="${ui.actionLink("ebolaexample", "overview/inpatientLocation", "startOutpatientVisit",
                [patient: patient.uuid])}">
            <i class="icon-exchange"></i>
            Outpatient visit
        </a>

        <% } else if (!currentWard) { %>

        <p class="current-ward">Not yet admitted</p>

        <em>(To Do: Triage form should automatically admit.)</em>

        <form method="POST"
              action="${ui.actionLink("ebolaexample", "overview/inpatientLocation", "admit", [patient: patient.uuid])}">
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

        <p class="current-ward">
            Current Assignment:
            <strong>
                ${ui.format(currentWard)},
                <span class="current-bed">${currentBed ? ui.format(currentBed) : "(No Bed)"}</span>
            </strong>
        </p>

        <div id="making-changes" ng-show="makingChange">

            <h3>Change Bed Assignment</h3>

            <div class="locationFormDiv">
                <h2>Select type of ward</h2>
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
                       ng-model="\$parent.changeToWard" btn-radio="ward" uncheckable>
                    {{ ward.display }}
                </label>
            </div>


            <div ng-show="changeToWard" class="locationFormDiv">

                <h2>Select the bed</h2>

                <div class="button-group" id="bed-group">
                    <label ng-repeat="bed in bedsInWard"
                           class="button {{changeToBed.uuid == bed.uuid ?'assigned':''}} {{strContains(bed.display, occupied)?'occupied-bed':''}}"
                           ng-model="\$parent.changeToBed" btn-radio="bed" uncheckable
                            >
                        {{ bed.display }}
                    </label>
                </div>
            </div>

            <br/>

            <button class="confirm right" ng-disabled="!changeToWard" ng-click="doTransfer()">Save</button>
            <button class="cancel" ng-click="cancel()">Cancel</button>
        </div>
        <% } %>
    </div>
</div>

<script type="text/javascript">
    angular.bootstrap("")
</script>
