<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeJavascript("uicommons", "angular.min.js")
    ui.includeJavascript("uicommons", "angular-resource.min.js")
    ui.includeJavascript("uicommons", "angular-common.js")

    ui.includeJavascript("ebolaexample", "tabletapp/resources.js")
    ui.includeJavascript("ebolaexample", "findPatientByWard.js")
    ui.includeCss("ebolaexample", "findPatientByWard.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "Find Patient By Ward" }
    ]
</script>

<div id="find-patient-by-ward" ng-controller="FindPatientByWardCtrl" <% if (param.ward) { %>ng-init="init('${ param.ward[0] }')"<% } %>>

    Select a ward:
    <ul class="wards">
        <li class="ward" ng-class="{ selected: ward.uuid == selectedWard.uuid }" ng-repeat="ward in wards" ng-click="selectWard(ward)">
            <strong>{{ ward.display }}</strong>
            <br/>
            {{ ward.bedAssignments.length }} patient(s)
        </li>
    </ul>

    <div ng-show="selectedWard">
        Select a patient:
        <div ng-hide="selectedWard.bedAssignments.length">
            None
        </div>
        <ul class="beds">
            <li class="bed" ng-repeat="bedAssignment in selectedWard.bedAssignments" ng-click="selectPatient(bedAssignment.patient)">
                {{ bedAssignment.patient.display }}
                <span class="bed-number">{{ bedAssignment.bed.display }}</span>
            </li>
        </ul>
    </div>

</div>

<script type="text/javascript">
    angular.bootstrap('#find-patient-by-ward', ['findPatientByWard']);
</script>