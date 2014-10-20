<div class="info-section">

    <div class="info-header">
        <i class="icon-calendar"></i>
        <h3>
            <% if (config.encounterType) { %>
                ${ ui.format(config.encounterType) }
            <% } else { %>
                Encounters
            <% } %>
            (this visit)
        </h3>
    </div>

    <div class="info-body">
        <% if (!config.activeVisit) { %>
            No active visit
        <% } else { %>
            <% if (encounters && encounters.size() > 0) { %>
                <ul>
                    <% encounters.each { %>
                    <li class="clear">
                        ${ ui.formatDatePretty(it.encounterDatetime) }
                        <% if (!config.encounterType) { %>
                        ${ ui.format(encounterType) }
                        <% } %>
                    </li>
                    <% } %>
                </ul>
            <% } else { %>
                ${ui.message("coreapps.none")}
            <% } %>
        <% } %>
    </div>
</div>