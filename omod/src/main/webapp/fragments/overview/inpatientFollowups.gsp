<%
    def entryLink = "/${ contextPath }/ms/uiframework/resource/ebolaexample/html/inpatientfollowup/index.html?" +
            "patientUuid=${ patient.patient.uuid }&" +
            "visitUuid=${ visit.visit.uuid }&" +
            "locationUuid=${ location.uuid }&" +
            "providerUuid=${ provider.person.uuid }"
%>
<div class="info-section">

    <div class="info-header">
        <i class="icon-calendar"></i>
        <h3>
            Inpatient Followup
        </h3>
    </div>

    <div class="info-body">
        <% if (!config.activeVisit) { %>
            No active visit
        <% } else { %>
            <div>
                <a class="button" href="${ entryLink }">Enter Form</a>
            </div>
            <% if (encounters && encounters.size() > 0) { %>
                <ul>
                    <% encounters.each { %>
                    <li class="clear">
                        ${ ui.formatDatetimePretty(it.encounterDatetime) }
                    </li>
                    <% } %>
                </ul>
            <% } else { %>
                ${ui.message("coreapps.none")}
            <% } %>
        <% } %>
    </div>
</div>