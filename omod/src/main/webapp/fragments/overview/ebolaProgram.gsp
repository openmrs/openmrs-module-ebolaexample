<%
    def triageEntryLink = ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithSimpleUi", [
            patientId: patient.patient.uuid,
            visitId: activeVisit?.visit?.uuid,
            definitionUiResource: "ebolaexample:htmlforms/triage.xml",
            returnUrl: ui.thisUrl()
    ])

    def triageViewLink = ui.pageLink("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [
            encounter: triageEncounter?.uuid,
            returnUrl: ui.thisUrl()
    ])
%>
<div class="info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>
        <h3>${ ui.message("ebolaexample.ebolaOverview.title") }</h3>
    </div>

    <div class="info-body">
        <% if (mostRecentWeight) { %>
            Weight: ${ ui.format(mostRecentWeight) } <small>(as of ${ ui.format(mostRecentWeight.obsDatetime) })</small>
        <% } else { %>
            Weight: not recorded
        <% } %>

        <% if (currentEnrollment == null) { %>

            <p>Not currently enrolled</p>

            <a class="big button" href="${ triageEntryLink }">
                Triage
            </a>

            <% if (triageEncounter) { %>
                <a href="${ triageViewLink }">
                    View Last Triage
                </a>
            <% } %>

        <% } else { %>

            <p>
                Enrolled since ${ ui.format(currentEnrollment.dateEnrolled) }
            </p>
            <% if (triageEncounter) { %>
                <a href="${ triageViewLink }">
                    View Triage
                </a>
            <% } else { %>
                <a class="big button" href="${ triageEntryLink }">
                    Triage
                </a>
            <% }%>
        <% } %>
    </div>
</div>