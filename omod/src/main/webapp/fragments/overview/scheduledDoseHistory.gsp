<%
    def dosesByOrder = doseHistory.dosesByOrder
    def dateFormat = new java.text.SimpleDateFormat("dd-MMM-yy")
    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

    def today = context.loadClass("org.openmrs.module.reporting.common.DateUtil").getStartOfDay(new Date())
    def isToday = {
        return it == today
    }
    def formatStatus = {
        def ret = it.name()
        if (ret == 'FULL') {
            return "Fully Given"
        } else if (ret == 'PARTIAL') {
            return "Partially Given"
        } else if (ret == 'NOT_GIVEN') {
            return "Not Given"
        }
        return ret;
    }
%>
<style type="text/css">
    .today {
        background-color: yellow;
    }
</style>

<table>
    <thead>
    <tr>
        <th>
        </th>
        <% dates.each { %>
            <th <% if (isToday(it)) { %>class="today"<% } %>>${ dateFormat.format(it) }</th>
        <% } %>
    </tr>
    </thead>
    <% doseHistory.groupedOrders.each { group -> %>
        <tbody>
            <tr>
                <th colspan="8">${ ui.format(group.key) }</th>
            </tr>
            <% group.value.each { order -> %>
            <tr>
                <td>${ ui.format(order) }</td>
                <% dates.each { day -> %>
                    <td>
                        <% doseHistory.getDosesFor(order, day).each { %>
                            ${ timeFormat.format(it.scheduledDatetime) }
                            ${ formatStatus(it.status) }
                            <% if (it.reasonNotAdministeredNonCoded) { %>
                                (${ it.reasonNotAdministeredNonCoded })
                            <% } %>
                            <br/>
                        <% } %>
                    </td>
                <% } %>
            </tr>
            <% } %>
        </tbody>
    <% } %>
</table>

<% doseHistory.groupedOrders.each { group -> %>



<% } %>