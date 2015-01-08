<%
    ui.includeJavascript("uicommons", "handlebars/handlebars.min.js")
    ui.includeCss("ebolaexample", "overview/ebolaOverview.css")
    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/template" id="last-encounter-template">
<!-- TO DO: do not make this template more complex! We need to provide a better representation, e.g. that knows about symptoms  -->
<ul>
    <li>
        <em>
            {{display location}}
            -
            {{date encounterDatetime}}
        </em>
    </li>
    {{#each obs}}
    <li>
        {{#if this.value}}
        {{display this.concept}}: {{display this.value}}
        {{else}}
        {{#each this.groupMembers}}
        {{display this.value}}
        {{/each}}
        {{/if}}
    </li>
    {{/each}}
</ul>
</script>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},

        <% if (wardAndBed && wardAndBed.ward) { %>
        {
            label: "${ ui.format(wardAndBed.ward) }",
            link: '${ ui.escapeJs(ui.pageLink("ebolaexample", "findPatientByWard", [ ward: wardAndBed.ward.uuid ])) }'
        },
        <% } %>

        <% if (patient) { %>
        {
            label: "<% patient.names.each { name -> def pName = name %> ${pName.fullName} <% } %>",
            link: '${ ui.escapeJs(ui.pageLink("ebolaexample", "ebolaOverview", [ patient: patient.uuid ])) }'
        },
        <% } %>

        {label: "Assign Bed"}
    ]
    var patient = {id: ${ patient.id }};

    Handlebars.registerHelper('display', function (obj) {
        return obj ? (obj.display ? obj.display : obj) : "";
    });
    Handlebars.registerHelper('date', function (obj) {
        return obj ? new Date(obj).toLocaleString() : "";
    });

    var lastEncounterTemplate = Handlebars.compile(jq('#last-encounter-template').html());
</script>

<div class="clear"></div>

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
        <h3>Assign patient to a bed</h3>
    </div>

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

        <p class="current-ward">${ui.format(currentWard)},
            <span class="current-bed">${currentBed ? ui.format(currentBed) : "(No Bed)"}</span>
        </p>

        <div id="making-changes" ng-show="makingChange">

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

                <div class="button-group" ng-show="changeToWard">
                    <label ng-repeat="bed in bedsForWard(changeToWard)" class="button {{changeToBed.uuid == bed.uuid ?'assigned':''}}"
                           ng-model="\$parent.changeToBed" btn-radio="bed"
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
