<%
    ui.includeJavascript("uicommons", "handlebars/handlebars.min.js")
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
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" ,
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.patient.id])}'},
        { label: "${ ui.escapeJs(ui.message("ebolaexample.ebolaOverview.title")) }" }
    ]
    var patient = { id: ${ patient.id } };

    Handlebars.registerHelper('display', function(obj) {
        return obj ? (obj.display ? obj.display : obj) : "";
    });
    Handlebars.registerHelper('date', function(obj) {
        return obj ? new Date(obj).toLocaleString() : "";
    });

    var lastEncounterTemplate = Handlebars.compile(jq('#last-encounter-template').html());
</script>

<% if (includeFragments) {
    includeFragments.each {
%>
    ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment) }
<%   }
} %>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient, activeVisit: activeVisit, appContextModel: appContextModel ]) }

<div class="clear"></div>

<div class="container">
    <div class="dashboard clear">
        <div class="info-container column">

            ${ ui.includeFragment("ebolaexample", "overview/ebolaProgram",
                    [ patient: patient ]) }

            ${ ui.includeFragment("ebolaexample", "overview/inpatientLocation",
                    [ patient: patient, activeVisit: activeVisit ]) }

        </div>

        <div class="info-container column">

            ${ ui.includeFragment("ebolaexample", "overview/inpatientFollowups",
                    [ patient: patient, activeVisit: activeVisit ]) }

            ${ ui.includeFragment("ebolaexample", "overview/mostRecentEncounter",
                    [ patient: patient, encounterType: followupEncounterType, handlebarsTemplate: "lastEncounterTemplate"]) }

        </div>

        <div class="action-container column">
            <div class="action-section">
                <ul>
                    <li>More actions here?</li>
                </ul>
            </div>
        </div>
    </div>
</div>