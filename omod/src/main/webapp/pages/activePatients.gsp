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

<form action="activePatients.page" style="margin-bottom: 20px;">
    <span>Ward: <select name="ward" style="display: inline;">
        <option value=""> All </option>
        <% wards.each { %>
        <option value="${it.uuid}"
            <% if (selectedWard && it.equals(selectedWard)) { %> selected <% } %>>${it}
        </option> <% } %>
    </select>
        <button type="submit">Load</button>
    </span>
</form>

<% assignments.each { %>
<strong> ${it.getWard()} </strong>
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
        def bedAssignments = it.getBedAssignments()
        bedAssignments.each { assignment ->
        def patient = assignment.value
        def bed = assignment.key
    %>
    <tr>
        <td width="20%">
            <a href="${ui.pageLink("ebolaexample", "ebolaOverview",
                    [patient: patient.uuid])}">${patient.patientIdentifier }</a>
        </td>
        <td width="50%">${ui.format(patient)} </td>
        <td> ${bed} </td>
    </tr>
    <% } %>
    </tbody>
</table>
<br>

<% } %>
