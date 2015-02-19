<%
    def triageEntryLink = ui.pageLink("htmlformentryui", "htmlform/enterHtmlFormWithSimpleUi", [
            patientId           : patient.patient.uuid,
            visitId             : activeVisit?.visit?.uuid,
            definitionUiResource: "ebolaexample:htmlforms/triage.xml",
            returnUrl           : ui.thisUrl()
    ])

    def triageViewLink = ui.pageLink("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [
            encounter: triageEncounter?.uuid,
            returnUrl: ui.thisUrl()
    ])
%>

<div class="info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>${ui.message("ebolaexample.ebolaOverview.title")}</h3>
    </div>

    <div class="info-body">
        <p>
            <% if (mostRecentWeight) { %>
            <strong>Weight:</strong>${ui.format(mostRecentWeight)} <small>(as of ${ui.format(mostRecentWeight.obsDatetime)})</small>
            <% } else { %>
            Weight: not recorded
            <% } %>
        </p>

        <p>
            <strong>Ebola Stage At Admission:</strong>
            <% if (ebolaStage) { %>
            ${ui.format(ebolaStage)}
            <% } else { %>
            --
            <% } %>
        </p>

        <p><strong>Type of Patient At Admission:</strong>
            <% if (typeOfPatient) { %>
            ${ui.format(typeOfPatient)}
            <% } else { %>
            --
            <% } %>
        </p>


        <% if (currentEnrollment == null) { %>
        <p>Not currently enrolled</p>
        <% } else { %>
        <p><strong>Enrolled since</strong> ${ui.format(currentEnrollment.dateEnrolled)}</p>
        <% } %>


        <% if (triageEncounter) { %>
        <a href="${triageViewLink}">
            View Triage
        </a>
        <% } else if (false /* disabled */) { %>
        <a class="big button" href="${triageEntryLink}">
            Triage
        </a>
        <% } %>

    </div>
</div>