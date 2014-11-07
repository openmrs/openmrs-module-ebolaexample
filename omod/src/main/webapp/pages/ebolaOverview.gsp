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

<div class="clear"></div>

<div class="container">
    <div class="dashboard clear">
        <div class="info-container column">

            ${ ui.includeFragment("ebolaexample", "overview/program",
                    [ patient: patient, program: program, title: ui.message("ebolaexample.ebolaOverview.title") ]) }

            ${ ui.includeFragment("ebolaexample", "overview/inpatientLocation",
                    [ patient: patient, activeVisit: activeVisit ]) }

        </div>

        <div class="info-container column">

            ${ ui.includeFragment("ebolaexample", "overview/encountersInActiveVisit",
                    [ patient: patient, activeVisit: activeVisit,
                      encounterType: followupEncounterType,
                      entryLinks: followupForms ]) }

            ${ ui.includeFragment("ebolaexample", "overview/mostRecentEncounter",
                    [ patient: patient, encounterType: followupEncounterType]) }

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