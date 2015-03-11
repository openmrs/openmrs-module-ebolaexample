<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {label: "${ ui.escapeJs(ui.message("ebolaexample.activePatients.title")) }"}
    ]
</script>

<script type="text/css">
    i, .home-icon, .small{
    font-size: 2.3em !important;
    }
</script>

<h2>View Active Patients:  ${today.format('dd MMM yyyy')}</h2>

<% assignments.each { %>
<strong> ${it.key} </strong>
<table>
    <thead>
    <tr>
        <th>Patient Number</th>
        <th>Patient Name</th>
        <th>Bed</th>
    </tr>
    </thead>

    <tbody>
    <%
        def bedAssignments = it.value
        bedAssignments.each { assignment ->
        def patient = assignment.patient
        def bed = assignment.bed
        def patientName = patient.display.split('-')[-1]
        def patientNumber = patient.display.split('-')[0..-2].join('-')
    %>
    <tr>
        <td width="20%">
            <a href="${ui.pageLink("ebolaexample", "ebolaOverview",
                    [patient: patient.uuid])}">${ patientNumber }</a>
        </td>
        <td width="50%">${patientName} </td>
        <td> ${bed.display} </td>
    </tr>
    <% } %>
    </tbody>
</table>
<br>

<% } %>
