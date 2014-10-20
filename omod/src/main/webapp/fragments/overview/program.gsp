<div class="info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>
        <h3>${ ui.format(config.program) }</h3>
    </div>

    <div class="info-body">
        <% if (currentEnrollment == null) { %>

            Not currently enrolled
            <a class="big button" href="${ ui.actionLink("ebolaexample", "overview/program", "enroll", [ patient: patient.patient.uuid, program: program.uuid ]) }">
                <i class="icon-plus"></i>
                Enroll in program
            </a>

        <% } else { %>

            Enrolled since ${ ui.format(currentEnrollment.dateEnrolled) }

        <% } %>
    </div>
</div>