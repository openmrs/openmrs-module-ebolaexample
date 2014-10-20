<%
    def EmrApiConstants = context.loadClass("org.openmrs.module.emrapi.EmrApiConstants")
%>
<div class="info-section">

    <div class="info-header">
        <i class="icon-hospital"></i>
        <h3>Location</h3>
    </div>

    <div class="info-body">
        <% if (!config.activeVisit) { %>

            No active visit. <br/>

            <a class="button" href="${ ui.actionLink("ebolaexample", "overview/assignedLocation", "startOutpatientVisit",
                    [ patient: patient.patient.uuid ]) }">
                <i class="icon-exchange"></i>
                Outpatient visit
            </a>

        <% } else if (!currentLocation) { %>

            Outpatient <br/>

            <form method="POST" action="${ ui.actionLink("ebolaexample", "overview/assignedLocation", "admit",
                                            [ patient: patient.patient.uuid ]) }">
                ${ ui.includeFragment("uicommons", "field/location", [
                        label: "Admit to",
                        formFieldName: "location",
                        withTag: EmrApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION
                ]) }
                <button type="submit">
                    <i class="icon-hospital"></i>
                </button>
            </form>

        <% } else { %>

            Inpatient at <strong>${ ui.format(currentLocation) }</strong> <br/>

            <form method="POST" action="${ ui.actionLink("ebolaexample", "overview/assignedLocation", "transfer",
                    [ patient: patient.patient.uuid ]) }">
                ${ ui.includeFragment("uicommons", "field/location", [
                        label: "Transfer to",
                        formFieldName: "location",
                        withTag: EmrApiConstants.LOCATION_TAG_SUPPORTS_TRANSFER
                ]) }
                <button type="submit">
                    <i class="icon-hospital"></i>
                </button>
            </form>
        <% } %>
    </div>
</div>