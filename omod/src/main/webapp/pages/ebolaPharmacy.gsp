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

<p>Number of patients = ${patients.size}</p>

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
    <% patients.each { p ->
        def names = p.names
        def identifiers = p.identifiers
        def location
    %>
    <tr>
        <td>
            <% names.each { name -> def pName = name %> ${pName.fullName} <% } %>
        </td>
        <td>
            <% identifiers.each { identifier ->
                def pIdentifier = identifier
                location = identifier.location
            %> ${pIdentifier.identifier} <% } %>
        </td>
        <td>
             ${location.name}
        </td>
    </tr>
    <% } %>
    </tbody>
</table>