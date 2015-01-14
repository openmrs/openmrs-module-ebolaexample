<%
    def dateFormat = new java.text.SimpleDateFormat("d.MMM HH:mm")
%>
<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>Prescriptions</h3>

        <% if (showAll) { %>
            <a class="right" href="${ ui.pageLink("ebolaexample", "ebolaOverview", [ patient: patient.patient.uuid ]) }">
                Back to Summary
            </a>
        <% } else { %>
            <a class="right" href="${ui.pageLink("ebolaexample", "allPrescriptions", [ patient: patient.patient.id ])}">
                View All
            </a>
        <% } %>

    </div>

    <div class="info-body">
        <% if (groupedOrders.size() > 0) { %>
            <ul>
                <% groupedOrders.each { group ->
                    def groupInactive = group.key.flags.contains("inactive");
                %>
                    <li class="prescription-group<% if (groupInactive) { %> inactive<% } %>">
                        <% if (group.key.flags.contains("recent")) { %>
                            <span class="lozenge recent">RECENT</span>
                        <% } %>
                        <strong>
                            ${ ui.format(group.key.concept) }
                        </strong>
                        <% if (group.key.drug) { %>
                            <em>
                                ${ ui.format(group.key.drug) }
                            </em>
                        <% } %>
                        <ul>
                            <% group.value.each { %>
                                <li class="clear">
                                    <span class="right">
                                        ${ dateFormat.format(it.dateActivated) }
                                        <% if (it.dateStopped) { %>
                                            -
                                            ${ dateFormat.format(it.dateStopped) }
                                        <% } else if (it.autoExpireDate) { %>
                                            -
                                            <em>${ dateFormat.format(it.autoExpireDate) }</em>
                                        <% } %>
                                    </span>
                                    <% if (!it.active) { %>
                                        <% if (!groupInactive) { %>
                                            <span class="lozenge stopped">
                                        <% } %>
                                        ${ it.dateStopped ? "STOPPED" : "EXPIRED" }
                                        <% if (!groupInactive) { %>
                                            </span>
                                        <% } %>
                                    <% } %>
                                    ${ it.dosingInstructionsInstance.getDosingInstructionsAsString(context.locale) }
                                </li>
                            <% } %>
                        </ul>
                    </li>
                <% } %>
            </ul>
        <% } else { %>
            ${ ui.message("coreapps.none") }
        <% } %>
    </div>
</div>