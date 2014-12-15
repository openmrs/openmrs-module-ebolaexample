<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        {icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm'},
        {label: "${ ui.escapeJs(ui.message("ebolaexample.ebolaPharmacy.title")) }"}
    ]
</script>

<h2>Pharmacy Overview ${ ui.format(today)}</h2>

<table>
    <thead>
    <tr>
        <th>Patient Number</th>
        <th>Patient Name</th>
        <th>Ward</th>
        <th>Drug</th>
    </tr>
    </thead>
    <tbody>
    <% ebolaPatients.each { ebolaPatient ->
        def names = ebolaPatient.patient.names
        def identifiers = ebolaPatient.patient.identifiers
        def location
        def drugOrders = ebolaPatient.drugOrders

    %>
    <tr>
        <td>
            <% identifiers.each { identifier ->
                def pIdentifier = identifier
                location = identifier.location
            %> ${pIdentifier.identifier} <% } %>
        </td>

        <td>
            <% names.each { name -> def pName = name %> ${pName.fullName} <% } %>
        </td>

        <td>
             ${location.name}
        </td>
        <td>
            <% drugOrders.each { drugOrder ->
                def pDrugOrder = drugOrder
            %> ${pDrugOrder.drug.name }<br> <% } %>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>