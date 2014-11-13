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
            <% } else if (encounter.form) { %>
                ${ ui.includeFragment("htmlformentryui", "htmlform/viewEncounterWithHtmlForm", [ encounter: lastEncounter ]) }
            <% } else { %>
                Programming error: cannot display encounter because no form or template is provided
            <% } %>
        <% } else { %>

            ${ui.message("coreapps.none")}

        <% } %>
    </div>
</div>

<% if (lastEncounter && config.handlebarsTemplate) { %>
    <script type="text/javascript">
        jq(function() {
            var html = ${ config.handlebarsTemplate }(${ lastEncounterJson });
            jq('#most-recent-encounter-info-body').html(html);
        });
    </script>
<% } %>