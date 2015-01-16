<%
    ui.includeCss("ebolaexample", "overview/ebolaOverview.css")
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        <% if (wardAndBed && wardAndBed.ward) { %>
        { label: "${ ui.format(wardAndBed.ward) }",
            link: '${ ui.escapeJs(ui.pageLink("ebolaexample", "findPatientByWard", [ ward: wardAndBed.ward.uuid ])) }' },
        <% } %>
        { label: "${ ui.format(patient.patient.familyName) }, ${ ui.format(patient.patient.givenName) }",
            link: '${ ui.pageLink("ebolaexample", "ebolaOverview", [ patient: patient.patient.uuid ]) }'},
        { label: "All Prescriptions" }
    ]
    var patient = { id: ${ patient.id } };
</script>

${ ui.includeFragment("ebolaexample", "overview/patientHeader", [ patient: patient.patient, activeVisit: activeVisit, appContextModel: appContextModel ]) }

<div class="clear"></div>

<div class="container">
    <div class="dashboard clear">
        <div class="long-info-container column">

            ${ ui.includeFragment("ebolaexample", "overview/prescriptions", [ patient: patient, showAll: true ]) }

        </div>
    </div>
</div>