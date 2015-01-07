<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {label: "${ ui.escapeJs(ui.message("ebolaexample.ebolaPharmacy.title")) }"}
    ]
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
        def names = ebolaPatient.patient.names
        def identifiers = ebolaPatient.patient.identifiers
        def ward = ebolaPatient.ward
        def bed = ebolaPatient.bed
        def drugOrders = ebolaPatient.drugOrders

    %>
    <tr>
        <td>
            <% identifiers.each { identifier ->
                def pIdentifier = identifier
                location = identifier.location
            %> <a href="${ui.pageLink("ebolaexample", "ebolaOverview",
                [patient: ebolaPatient.patient.uuid])}">${pIdentifier.identifier}</a> <% } %>
        </td>

        <td>
            <% names.each { name -> def pName = name %> ${pName.fullName} <% } %>
        </td>

        <td>
            <% if (ward != null) { %> ${ward.name} <% } %>
            <% if (bed != null) { %> ( ${bed.name} ) <% } %>
        </td>
        <td>
            <% drugOrders.each { drugOrder ->
                def pDrugOrder = drugOrder
            %> ${pDrugOrder.drug.name}<br> <% } %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>