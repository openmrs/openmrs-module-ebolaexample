<div class="info-section">

    <div class="info-header">
        <i class="icon-medkit"></i>

        <h3>New/Updated Prescriptions</h3>
    </div>

    <div class="info-body">
        <% if (newUpdatedDrugOrders && newUpdatedDrugOrders.size() > 0) { %>
        <ul>
            <% newUpdatedDrugOrders.each { %>
            <li class="clear">
                ${it.concept.displayString}<br>
                <em>${it.drug.name}
                ${it.route.displayString } - ${it.duration } Days - ${it.dose } - ${it.dosingInstructions}</em>
            </li>
            <% } %>
        </ul>
        <% } else { %>
        ${ui.message("coreapps.none")}
        <% } %>
    </div>
</div>