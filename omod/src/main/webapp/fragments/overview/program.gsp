<div class="info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>
        <h3>${ config.title ?: ui.format(config.program) }</h3>
    </div>

    <div class="info-body">
        <% if (currentEnrollment == null) { %>

            <p>Not currently enrolled</p>

            <% if (enrollmentForm) { %>
                <a class="big button" href="${ enrollmentForm.link }">
                    <% if (enrollmentForm.icon) { %>
                        <i class="${ enrollmentForm.icon }"></i>
                    <% } %>
                    ${ enrollmentForm.label }
                </a>
            <% } else { %>
                <a class="big button" href="${ ui.actionLink("ebolaexample", "overview/program", "enroll", [ patient: patient.patient.uuid, program: program.uuid ]) }">
                    <i class="icon-plus"></i>
                    Enroll in program
                </a>
            <% } %>
        <% } else { %>

            Enrolled since ${ ui.format(currentEnrollment.dateEnrolled) }

        <% } %>
    </div>
</div>