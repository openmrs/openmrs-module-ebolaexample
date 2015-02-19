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
        {label: "${ ui.escapeJs(ui.message("ebolaexample.ebolaOverview.title")) }"}
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

<% if (includeFragments) {
    includeFragments.each {
%>
${ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment)}
<% }
} %>

${ui.includeFragment("ebolaexample", "overview/patientHeader", [patient: patient.patient, activeVisit: activeVisit, appContextModel: appContextModel])}

<div class="clear"></div>

<div class="container">

    <a href="/${ ui.contextPath() }/ms/uiframework/resource/ebolaexample/html/tabletapp/index.html#/patients/${ patient.patient.uuid }/overview/${ patient.patient.uuid }/">
        Add a Prescription
    </a>

    <div class="dashboard clear">

        <div class="info-container column">
            ${ui.includeFragment("ebolaexample", "overview/ebolaProgram", [patient: patient])}
        </div>

        <div class="long-info-container column">
            ${ui.includeFragment("ebolaexample", "overview/inpatientLocation", [patient: patient, activeVisit: activeVisit])}
        </div>

        <div class="clear"></div>

        <div class="long-info-container column">
            ${ui.includeFragment("ebolaexample", "overview/prescriptions", [patient: patient])}
            <br/>
            ${ui.includeFragment("ebolaexample", "overview/scheduledDoseHistory", [patient: patient])}
        </div>

        <div class="info-container column" style="display: none">

            ${ui.includeFragment("ebolaexample", "overview/inpatientFollowups", [patient: patient, activeVisit: activeVisit])}

            ${ui.includeFragment("ebolaexample", "overview/mostRecentEncounter",
                    [patient: patient, encounterType: followupEncounterType, handlebarsTemplate: "lastEncounterTemplate"])}

        </div>

        <div class="action-container column" style="display: none">
            <div class="action-section">
                <ul>
                    <li>More actions here?</li>
                </ul>
            </div>
        </div>
    </div>
</div>