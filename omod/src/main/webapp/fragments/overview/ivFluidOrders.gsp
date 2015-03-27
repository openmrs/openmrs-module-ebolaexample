<%
    def dateFormat = new java.text.SimpleDateFormat("d.MMM HH:mm")
%>
<div class="long-info-section">

    <div class="info-header" id="ivfluids">
        <i class="icon-medkit"></i>

        <h3>IV Fluids</h3>

        <% if (showAll) { %>
        <a href="${ui.pageLink("ebolaexample", "ebolaOverview", [patient: patient.patient.uuid])}">
            Back to Summary
        </a>
        <% } else { %>
        <a href="${ui.pageLink("ebolaexample", "allFluidOrders", [patient: patient.patient.id])}">
            View All
        </a>
        ${ui.includeFragment("ebolaexample", "overview/actions", [patient: patient, currentAssignment:wardAndBed])}
        <% } %>
    </div>

    <div class="info-body">
        <% if (orders.size() > 0) { %>
            <ul>
                <% orders.each { orderView ->
                    def order = orderView.order
                    def inactive = (orderView.lastStatus == "STOPPED:");
                %>
                <li class="iv-fluid-order<% if (inactive) { %> inactive<% } %>">
                    <div>
                        <span class="concept-name"><strong>${ui.format(order.getConcept())}</strong></span>
                        <em>${fluidOrderFormatter.formatIvFluidOrder(order, ui)}</em>
                    </div>
                    <div class="right">
                        ${dateFormat.format(order.dateCreated)}
                    </div>
                    <span class="prescriber">
                        <% if(order.orderer) { %>
                        <em> ordered by ${order.orderer.getName()} </em>
                        <% } else {%>
                        <em> ordered by Unknown User </em>
                        <% } %>
                    </span>
                    <span>
                        - ${orderView.lastStatus}
                        <% if (orderView.lastStatusChange) { %>
                            ${dateFormat.format(orderView.lastStatusChange)}
                        <% } %>
                    </span>

                </li>
                <% } %>
            </ul>
        <% } else { %>
            ${ui.message("coreapps.none")}
        <% } %>

        <div style="clear:both;"></div>
    </div>
    <div>
        <a href="#" class="right back-to-top">Back to top</a>
    </div>
</div>
