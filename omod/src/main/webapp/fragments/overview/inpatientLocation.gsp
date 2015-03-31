<%
    def EmrApiConstants = context.loadClass("org.openmrs.module.emrapi.EmrApiConstants")

    ui.includeCss("ebolaexample", "overview/inpatientLocation.css")
%>


    <span style="width: 50%; display:inline-table">
        <div>
            <strong>Ebola stage:</strong>
            <span>
                <% if (config.ebolaStage) { %>
                    ${ui.format(config.ebolaStage)}
                <% } else if (config.ebolaStageAtAdmission) { %>
                    ${ui.format(config.ebolaStageAtAdmission)}
                <% } else { %>
                --
                <% } %>

            </span>
        </div>
        <div>

        <strong>Location:</strong>
            <% if (currentOutcome) { %>
            <span >
                Out of ETC
            </span>

        <% } else { %>

            <div style="display: inline;">

                <% if (!config.activeVisit) { %>

                <em>No active visit.</em> <br/>

                <a class=""
                   href="${ui.actionLink("ebolaexample", "overview/inpatientLocation", "startOutpatientVisit",
                           [patient: patient.patient.uuid])}">
                    <i class="icon-exchange"></i>
                    Outpatient visit
                </a>

                <% } else if (!currentWard) { %>

                --
                <% } else { %>

                <p class="current-ward">${ui.format(currentWard)},
                    <span class="current-bed">${currentBed ? ui.format(currentBed) : "(No Bed)"}</span>

                </p>


                <% } %>
                <a class="right" style="margin-right: 60px" href="${ui.pageLink("ebolaexample", "changeInPatientLocation",
                    [patientUuid: patient.patient.uuid])}">Change</a>

            </div>
        <% } %>
        </div>

        <div style="">
            <span style=""><strong>Ebola Treatment Outcome:</strong>
                <em>
                    <% if (currentOutcome) { %>
                    ${ui.format(currentOutcome)}, ${patientProgram.dateCompleted.format('dd MMM yyyy, HH:mm')}
                    <% } else { %>None<% } %>
                </em>
            </span>

            <% if (!currentOutcome) { %>
            <a class="right" style="margin-right: 60px" href="${ui.pageLink("ebolaexample", "changePatientDischarge",
                    [patientUuid: patient.patient.uuid])}">Change</a>
            <% } %>
        </div>

        <div style="clear: both;"></div>
    </span>
