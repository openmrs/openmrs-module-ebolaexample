<div class="long-info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>Prescriptions</h3>
    </div>

    <div class="info-body">
        <% if (groupedOrders.size() > 0) { %>
            <ul>
                <% groupedOrders.each { group -> %>
                    <li>
                        <strong>
                            ${ ui.format(group.key.concept) }
                            ${ ui.format(group.key.route) }
                        </strong>
                        <ul>
                            <% group.value.each { %>
                                <li class="clear">
                                    <span class="right">
                                        ${ ui.format(it.dateActivated) }
                                        <% if (it.dateStopped) { %>
                                            <br/>
                                            ${ ui.format(it.dateStopped) }
                                        <% } else if (it.autoExpireDate) { %>
                                            <br/>
                                            <em>- ${ ui.format(it.autoExpireDate) }</em>
                                        <% } %>
                                    </span>
                                    <% if (!it.active) { %>
                                        <span class="lozenge stopped">STOPPED ${ ui.format(it.effectiveStopDate) }</span>
                                    <% } %>
                                    <% if (recentOrders.contains(it)) { %>
                                        <span class="lozenge recent">RECENT</span>
                                    <% } %>
                                    <% if (it.asNeeded) { %>
                                        <span class="lozenge prn">${it.asNeededCondition ?: "As Needed"}</span>
                                    <% } %>
                                    <% if (it.drug) { %>
                                        ${ ui.format(it.drug) }
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