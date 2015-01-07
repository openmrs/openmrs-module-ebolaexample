<%
    def dateFormat = new java.text.SimpleDateFormat("d.MMM HH:mm")
%>
<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>Prescriptions</h3>
    </div>

    <div class="info-body">
        <% if (groupedOrders.size() > 0) { %>
            <ul>
                <% groupedOrders.each { group -> %>
                    <li class="prescription-group<% if (group.key.flags.contains("inactive")) { %> inactive<% } %>">
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
                                        <span class="lozenge stopped">STOPPED</span>
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