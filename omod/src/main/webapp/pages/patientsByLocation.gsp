<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("ebolaexample", "patientsByLocation.css")
%>
<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.escapeJs(ui.message("ebolaexample.patientsByLocation.title")) }" }
    ]
</script>

<%
    def groups = [ observationAreas, hotZoneAreas ]
    groups.each { group ->
%>
    <div class="column">
        <% group.each { location ->
            def rows = byLocation[location]
        %>
            <h3>${ ui.format(location) }</h3>
            <table>
                <tbody>
                <% if (!rows) { %>
                    <tr>
                        <td colspan="5">None</td>
                    </tr>
                <% } %>
                <% rows.each { row -> %>
                    <tr>
                        <td>${ row.getColumnValue("identifier") }</td>
                        <td>${ row.getColumnValue("familyName") }</td>
                        <td>${ row.getColumnValue("givenName") }</td>
                        <td>${ row.getColumnValue("birthdate") }</td>
                        <td>${ row.getColumnValue("gender") }</td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
<% } %>