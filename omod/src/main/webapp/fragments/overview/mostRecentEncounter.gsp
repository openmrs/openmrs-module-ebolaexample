<div class="info-section">

    <div class="info-header">
        <i class="icon-stethoscope"></i>
        <h3>
            <% if (config.encounterType) { %>
                Most recent ${ ui.format(config.encounterType) }
            <% } else { %>
                Most recent encounter
            <% } %>
        </h3>
    </div>

    <div class="info-body">
        <% if (lastEncounter) { %>

            ${ ui.includeFragment("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [ encounter: lastEncounter ]) }

        <% } else { %>

            ${ui.message("coreapps.none")}

        <% } %>
    </div>
</div>