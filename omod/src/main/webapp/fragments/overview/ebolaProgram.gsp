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

<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>${ui.message("ebolaexample.ebolaOverview.title")}</h3>
        <span class="right">

        <% if (activeVisit) { %>
        <% def visit = activeVisit.visit %>

        <span class="active-visit-started-at-message">
            ${ui.message("coreapps.patientHeader.activeVisit.at", activeVisitStartDatetime)}
        </span>
        <% if (activeVisit.admitted) { %>
        <span class="active-visit-message">
            ${ui.message("coreapps.patientHeader.activeVisit.inpatient", ui.format(activeVisit.latestAdtEncounter.location))}
        </span>
        <% } else { %>
        <span class="active-visit-message">
            ${ui.message("coreapps.patientHeader.activeVisit.outpatient")}
        </span>
        <% } %>
        <% } else if(currentOutcome) { %>
            <span class="active-visit-message">
                Out of ETC
            </span>
        <% } %>

        </span>

    </div>
    <div class="info-body">
        ${ui.includeFragment("ebolaexample", "overview/inpatientLocation",
                [patient: patient, activeVisit: activeVisit, ebolaStage:ebolaStage, ebolaStageAtAdmission:ebolaStageAtAdmission])}
        <span class="">

        <div>
            <strong>Weight:</strong>
            <em>
                <% if (mostRecentWeight) { %>
                ${ui.format(mostRecentWeight)}kg <small>(as of ${ui.format(mostRecentWeight.obsDatetime)})</small>
                <% } else { %>
                not recorded
                <% } %>
            </em>
        </div>

        <div>
            <strong>Ebola Stage At Admission:</strong>
            <em>
                <% if (ebolaStageAtAdmission) { %>
                ${ui.format(ebolaStageAtAdmission)}
                <% } else { %>
                --
                <% } %>
            </em>
        </div>

        <div>
            <strong>Type of Patient At Admission:</strong>
            <em>
                <% if (typeOfPatient) { %>
                ${ui.format(typeOfPatient)}
                <% } else { %>
                --
                <% } %>
            </em>
        </div>

        <div>
            <strong>Enrollment Status:</strong>
            <em>
                <% if (currentEnrollment == null) { %>
                Not currently enrolled
                <% } else { %>
                Enrolled since ${ui.format(currentEnrollment.dateEnrolled)}
                <% } %>
            </em>
        </div>

        <% if (triageEncounter) { %>
        <a href="${triageViewLink}">
            View Triage
        </a>
        <% } else if (false /* disabled */) { %>
        <a class="big button" href="${triageEntryLink}">
            Triage
        </a>
        <% } %>
        </span>

    </div>
</div>