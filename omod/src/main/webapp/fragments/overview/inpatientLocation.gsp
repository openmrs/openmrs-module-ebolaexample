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

    <div class="info-body" ng-app="inpatientLocation" ng-controller="InpatientLocationCtrl" ng-init="init({patientUuid:'${ patient.patient.uuid }'})">
        <% if (!config.activeVisit) { %>

            No active visit. <br/>

            <a class="button" href="${ ui.actionLink("ebolaexample", "overview/inpatientLocation", "startOutpatientVisit",
                    [ patient: patient.patient.uuid ]) }">
                <i class="icon-exchange"></i>
                Outpatient visit
            </a>

        <% } else if (!currentWard) { %>

            <p class="current-ward">Not yet admitted</p>

            <em>(To Do: Triage form should automatically admit.)</em>

            <form method="POST" action="${ ui.actionLink("ebolaexample", "overview/inpatientLocation", "admit",
                                            [ patient: patient.patient.uuid ]) }">
                ${ ui.includeFragment("uicommons", "field/location", [
                        label: "Admit to",
                        formFieldName: "location",
                        withTag: EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION
                ]) }
                <button type="submit">
                    Admit
                </button>
            </form>

        <% } else { %>

            <p class="current-ward">${ ui.format(currentWard) }</p>
            <p class="current-bed">${ currentBed ? ui.format(currentBed) : "(No Bed)" }</p>

            <button class="small" ng-hide="makingChange" ng-click="makingChange = true">
                Change
            </button>

            <div id="making-changes" ng-show="makingChange">
                <div>
                    <label ng-repeat="wardType in wardTypes" class="button" ng-model="\$parent.changeToWardType" btn-radio="wardType" uncheckable ng-hide="\$parent.changeToWardType && \$parent.changeToWardType != wardType">
                        {{ wardType.display }}
                    </label>
                </div>

                <div ng-show="changeToWardType">
                    <label ng-repeat="ward in wardsOfType(changeToWardType)" class="button" ng-model="\$parent.changeToWard" btn-radio="ward" uncheckable ng-hide="\$parent.changeToWard && \$parent.changeToWard != ward">
                        {{ ward.display }}
                    </label>
                </div>


                <div ng-show="changeToWard">
                    <div class="button-group" ng-show="changeToWard">
                        <label ng-repeat="bed in bedsForWard(changeToWard)" class="button" ng-model="\$parent.changeToBed" btn-radio="bed" uncheckable>
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