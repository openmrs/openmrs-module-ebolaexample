<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {label: "${ ui.escapeJs(ui.message("ebolaexample.ebolaPharmacy.title")) }"}
    ]
</script>

<script type="text/css">
    i, .home-icon, .small{
    font-size: 2.3em !important;
    }
</script>

<h2>Pharmacy Overview:  ${today.format('dd MMM yyyy')}</h2>

<form action="ebolaPharmacy.page" style="margin-bottom: 20px;">
    <span>Ward: <select name="ward" style="display: inline;">
        <% wards.each { %>
        <option value="${it.uuid}"
            <% if (selectedWard && it.equals(selectedWard)) { %> selected <% } %>>${it}
        </option> <% } %>
    </select>
        <button type="submit">Load</button>
    </span>
</form>

<table>
    <thead>
    <tr>
        <th>Patient Number</th>
        <th>Patient Name</th>
        <th>Ward</th>
        <th>Latest Prescriptions (24 hours)</th>
    </tr>
    </thead>
    <tbody>
    <% ebolaPatients.each { ebolaPatient ->
        def ward = ebolaPatient.ward
        def bed = ebolaPatient.bed
        def drugOrders = ebolaPatient.drugOrders

    %>
    <tr>
        <td>
            <a href="${ui.pageLink("ebolaexample", "ebolaOverview",
                    [patient: ebolaPatient.patient.uuid])}">${ ebolaPatient.patient.patientIdentifier }</a>
        </td>

        <td>
            ${ ui.format(ebolaPatient.patient) }
        </td>

        <td>
            <% if (ward != null) { %> ${ ui.format(ward) } <% } %>
            <% if (bed != null) { %> ( ${ ui.format(bed) } ) <% } %>
        </td>
        <td>
            <% drugOrders.each { drugOrder ->
                def pDrugOrder = drugOrder
            %> ${ ui.format(pDrugOrder.drug) }<br> <% } %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>