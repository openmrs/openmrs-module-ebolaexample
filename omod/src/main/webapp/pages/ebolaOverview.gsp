<%
    ui.decorateWith("appui", "standardEmrPage")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }" ,
            link: '${ui.pageLink("coreapps", "clinicianfacing/patient", [patientId: patient.patient.id])}'},
        { label: "${ ui.escapeJs(ui.message("ebolaexample.ebolaOverview.title")) }" }
    ]
    var patient = { id: ${ patient.id } };
</script>

<% if (includeFragments) {
    includeFragments.each {
%>
    ${ ui.includeFragment(it.extensionParams.provider, it.extensionParams.fragment) }
<%   }
} %>

${ ui.includeFragment("coreapps", "patientHeader", [ patient: patient.patient, activeVisit: activeVisit, appContextModel: appContextModel ]) }

<h2>${ ui.message("ebolaexample.ebolaOverview.title") }</h2>

<div class="clear"></div>

<div class="container">
    <div class="dashboard clear">
        <div class="info-container column">

            ${ ui.includeFragment("ebolaexample", "overview/program",
                    [ patient: patient, program: program ]) }

            ${ ui.includeFragment("ebolaexample", "overview/inpatientLocation",
                    [ patient: patient, activeVisit: activeVisit ]) }

        </div>

        <div class="info-container column">

            ${ ui.includeFragment("ebolaexample", "overview/encountersInActiveVisit",
                    [ patient: patient, activeVisit: activeVisit, encounterType: followupEncounterType ]) }

            ${ ui.includeFragment("ebolaexample", "overview/mostRecentEncounter",
                    [ patient: patient, encounterType: followupEncounterType]) }

        </div>

        <div class="action-container column">
            <div class="action-section">
                <% if (activeVisit) { %>
                    <ul>
                        <h3>${ ui.message("coreapps.clinicianfacing.activeVisitActions") }</h3>
                        <% visitActions.each { ext -> %>
                            <li>
                                <a href="${ ui.escapeJs(ext.url("/" + ui.contextPath(), appContextModel, ui.thisUrl())) }" id="${ ext.id }">
                                    <i class="${ ext.icon }"></i>
                                    ${ ui.message(ext.label) }
                                </a>
                            </li>
                        <% } %>
                    </ul>
                <% } %>

                <ul>
                    <h3>${ ui.message("coreapps.clinicianfacing.overallActions") }</h3>
                    <%
                        overallActions.each { ext -> %>
                    <a href="${ ui.escapeJs(ext.url("/" + ui.contextPath(), appContextModel, ui.thisUrl())) }" id="${ ext.id }">
                        <li>
                            <i class="${ ext.icon }"></i>
                            ${ ui.message(ext.label) }
                        </li>
                    </a>
                    <% } %>
                </ul>
            </div>
        </div>
    </div>
</div>