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
            <div>
                <% config.entryLinks.each { %>
                    <a class="button" href="${ ui.escapeAttribute(it.value) }">+ ${ it.key }</a>
                <% } %>
            </div>
            <% if (encounters && encounters.size() > 0) { %>
                <ul>
                    <% encounters.each { %>
                    <li class="clear">
                        <a class="visit-link" href="${ ui.pageLink("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [ encounter: it.uuid, returnUrl: ui.thisUrl() ]) }">
                            ${ ui.formatDatetimePretty(it.encounterDatetime) }
                            <% if (!config.encounterType) { %>
                                ${ ui.format(encounterType) }
                            <% } %>
                        </a>
                    </li>
                    <% } %>
                </ul>
            <% } else { %>
                ${ui.message("coreapps.none")}
            <% } %>
        <% } %>
    </div>
</div>