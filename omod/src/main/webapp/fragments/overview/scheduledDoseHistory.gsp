<%
    def dateFormat = new java.text.SimpleDateFormat("dd-MMM-yy")
    def timeFormat = new java.text.SimpleDateFormat("HH:mm")

    def OpenmrsUtil = context.loadClass("org.openmrs.util.OpenmrsUtil")
    def DateUtil = context.loadClass("org.openmrs.module.reporting.common.DateUtil")
    def today = DateUtil.getStartOfDay(new Date())

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
    def onDay = { date, day ->
        return date &&
                date.year == day.year &&
                date.month == day.month &&
                date.date == day.date
    }
    def activeOnDay = { order, day ->
        return OpenmrsUtil.compare(order.effectiveStartDate, DateUtil.getEndOfDay(day)) <= 0 &&
                OpenmrsUtil.compareWithNullAsLatest(order.effectiveStopDate, DateUtil.getStartOfDay(day)) >= 0;
    }
%>
<style type="text/css">
    .today {
        background-color: yellow;
    }

    .group th {
        text-align: left;
    }

    td.inactive {
        background-color: #d3d3d3;
    }

    #next {
        float: right;
    }
</style>

<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>Med Administration</h3>
    </div>

    <div class="info-body">
        <% if (prevDate) { %>
        <a id="prev" class="button" href="${ ui.pageLink("ebolaexample", "doseHistory", [ patient: patient.patient.uuid, toDate: prevDate ]) }">
            <i class="icon-arrow-left"></i>
            Previous week
        </a>
        <% } %>

        <% if (nextDate) { %>
        <a id="next" class="button" href="${ ui.pageLink("ebolaexample", "doseHistory", [ patient: patient.patient.uuid, toDate: nextDate ]) }">
            <i class="icon-arrow-right"></i>
            Next week
        </a>
        <% } %>

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
            <% if (doseHistory.orders.size() == 0) { %>
            <tbody>
            <tr>
                <td colspan="8">No prescriptions during this time period</td>
            </tr>
            </tbody>
            <% } %>
            <% doseHistory.ordersGroupedByDrug.each { group -> %>
            <tbody>
            <tr class="group">
                <th colspan="8">
                    <strong>
                        ${ ui.format(group.key.concept) }
                    </strong>
                    <em>
                        ${ ui.format(group.key) }
                    </em>
                </th>
            </tr>
            <% group.value.each { order -> %>
            <tr>
                <td>
                    ${ order.dosingInstructionsInstance.getDosingInstructionsAsString(context.locale) }
                </td>
                <% dates.each { day -> %>
                <td <% if (!activeOnDay(order, day)) { %>class="inactive"<% } %>
                >
                    <% if (onDay(order.effectiveStartDate, day)) { %>
                        START<br/>
                    <% } %>
                    <% doseHistory.getDosesFor(order, day).each { %>
                        ${ timeFormat.format(it.scheduledDatetime) }
                        ${ formatStatus(it.status) }
                        <% if (it.reasonNotAdministeredNonCoded) { %>
                            <em>${ it.reasonNotAdministeredNonCoded }</em>
                        <% } %>
                        <br/>
                    <% } %>
                    <% if (onDay(order.effectiveStopDate, day)) { %>
                        ${ order.dateStopped ? "STOP" : "EXPIRED" }<br/>
                    <% } %>
                </td>
                <% } %>
            </tr>
            <% } %>
            </tbody>
            <% } %>
        </table>
    </div>

</div>