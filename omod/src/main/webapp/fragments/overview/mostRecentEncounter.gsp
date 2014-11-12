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

    <div class="info-body" id="most-recent-encounter-info-body">
        <% if (lastEncounter) { %>
            <% if (config.handlebarsTemplate) { %>
                ...
            <% } else { %>
                ${ ui.includeFragment("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [ encounter: lastEncounter ]) }
            <% } %>
        <% } else { %>

            ${ui.message("coreapps.none")}

        <% } %>
    </div>
</div>

<% if (config.handlebarsTemplate) { %>
    <script type="text/javascript">
        jq(function() {
            var html = ${ config.handlebarsTemplate }(${ lastEncounterJson });
            jq('#most-recent-encounter-info-body').html(html);
        });
    </script>
<% } %>