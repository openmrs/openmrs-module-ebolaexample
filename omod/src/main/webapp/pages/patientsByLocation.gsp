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
    locationsByGroup.each {
    def group = it.value
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
                        <td>
                            <a href="${ ui.pageLink("ebolaexample", "ebolaOverview",
                                    [ patient: row.getColumnValue("patientId")]) }">
                                ${ row.getColumnValue("identifier") }
                            </a>
                        </td>
                        <td>
                            ${ row.getColumnValue("familyName") },
                            ${ row.getColumnValue("givenName") } <br/>
                            ${ row.getColumnValue("birthdate") },
                            ${ row.getColumnValue("gender") }
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% } %>
    </div>
<% } %>